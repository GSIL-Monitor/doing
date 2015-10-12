#!/usr/bin/env python
# coding:utf-8
import os

import subprocess
import datetime
import lazyxml
import sys
import shutil
import cPickle

reload(sys)
sys.setdefaultencoding('utf-8')

HUDSON_TASK = 'msearch-git-foundation'
GIT_ROOT = 'D:\\ci-root\\workspace\\{0}'.format(HUDSON_TASK)
#GIT_ROOT = '/Users/liumingbin/Workspace/360/git-svn/haosou'
PUBLISH_TITLE = u'好搜App基础版提测'
PUBLISH_SERVER = '\\\\10.16.16.32\\AutoRunProject\\AutoMan_haosou'


def get_git_log(date_from, date_to):
    proc = subprocess.Popen(['git', 'log', '--since', '{0} 00:00:00'.format(date_from.strftime('%Y-%m-%d')),
                             '--until', '{0} 00:00:00'.format(date_to.strftime('%Y-%m-%d')), '--pretty=BEGIN%n%cn%n-----%n%s%n-----%n%b%n-----%n%H%nEND', '--reverse'],
                            stdout=subprocess.PIPE, stderr=subprocess.PIPE, cwd=GIT_ROOT)
    return proc.stdout


def parse_git_log(reader):
    log_item = {'committer': '', 'title': '', 'content': '', 'type': ''}
    log_item_set = []
    current_state = ''

    for line in reader.readlines():
        if line.strip() == "":
            continue
        if line.strip().find('Change-Id:') == 0:
            continue

        line = line.decode('utf-8')

        if line[:5] == 'BEGIN':
            current_state = 'committer'
            log_item['committer'] = ''
            log_item['title'] = ''
            log_item['content'] = ''
            log_item['hash'] = ''
            continue

        if line[:5] == '-----' and current_state == 'committer':
            current_state = 'title'
            continue

        if line[:5] == '-----' and current_state == 'title':
            current_state = 'content'
            continue

        if line[:5] == '-----' and current_state == 'content':
            current_state = 'hash'
            continue

        if line[:3] == 'END' and current_state == 'hash':
            log_item_set.append(log_item.copy())
            continue

        log_item[current_state] += line

    return log_item_set


def get_commit_type(log_item):
    commit_types = ["bug", "demand", "optimize", "merge", "other"]

    type_weights = []
    for item in commit_types:
        type_weights.append(log_item['content'].lower().find(item) - 0.5)
        type_weights.append(log_item['title'].lower().find(item))

    min_weight = sys.maxint
    min_weight_idx = 0
    for index, item in enumerate(type_weights):
        if item <= -1:
            continue
        if item < min_weight:
            min_weight = item
            min_weight_idx = index

    if min_weight == sys.maxint:
        return "other"
    else:
        return commit_types[min_weight_idx / 2]


def get_commit_description(log_item):
    description = log_item["content"]
    if len(description) == 0:
        description = log_item["title"]

    return description


def catalog_commit_info(log_item_set):
    container = {}
    for log_item in log_item_set:
        commit_type = get_commit_type(log_item)
        commit_description = get_commit_description(log_item)
        commit_author = log_item["committer"]
        question_obj = {"description": commit_description, "author": commit_author}
        if commit_type not in container:
            container[commit_type] = []

        container[commit_type].append(question_obj)

    root = {"root": {
        "title": PUBLISH_TITLE,
        "container": []
    }}

    for container_type in container.keys():
        node = {"type": container_type,
                "question": container[container_type]}

        root["root"]["container"].append(node)

    return root


def generate_filename():
    return datetime.datetime.now().strftime('%Y-%m-%d_%Y%m%d%H%M%S.xml')


def publish_to_remote(xml):
    xml_file = generate_filename()
    with open(xml_file, 'w') as f:
        f.write(xml)
    remote_file = os.path.join(PUBLISH_SERVER, xml_file)
    shutil.copy(xml_file, remote_file)
    os.remove(xml_file)


def persist_log(log_to_persist):
    with open("log_persist.db", 'w') as f:
        cPickle.dump(log_to_persist, f)


def load_persist_log():
    if not os.path.exists("log_persist.db"):
        return []

    with open("log_persist.db", 'r') as f:
        return cPickle.load(f)


def remove_duplicate_log(prev_log, git_log_parsed):
    return [i for i in git_log_parsed if i not in prev_log]

def main():
    date_from = datetime.datetime.today()
    date_to = date_from + datetime.timedelta(days=1)
    prev_log = load_persist_log()
    git_log = get_git_log(date_from, date_to)
    git_log_parsed = parse_git_log(git_log)
    persist_log(git_log_parsed)
    git_log_parsed = remove_duplicate_log(prev_log, git_log_parsed)
    log_info_cataloged = catalog_commit_info(git_log_parsed)
    publish_info_xml = lazyxml.dumps(log_info_cataloged, cdata=False, encoding='utf-8')
    publish_to_remote(publish_info_xml)


if __name__ == '__main__':
    main()
