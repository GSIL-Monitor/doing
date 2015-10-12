#!/usr/bin/env python
#coding:gbk

import subprocess
import datetime
import smtplib, mimetypes
import os.path
import xml.etree.ElementTree
import sys
from email.mime.text import MIMEText
from email.mime.image import MIMEImage
from email.mime.multipart import MIMEMultipart

HUDSON_TASK = 'msearch-git-foundation'
GIT_ROOT = 'D:\\ci-root\\workspace\\{0}'.format(HUDSON_TASK)
JOB_BUILDS_PATH = 'D:\\Hudson\\.hudson\\jobs\\{0}\\builds'.format(HUDSON_TASK)
BUILD_HISTORY_FILE = '_runmap.xml'
MAIL_TITLE = '搜索App基础版'

def getGITLog(dateFrom, dateTo):
	proc = subprocess.Popen(['git', 'log', '--since', '{0} 00:00:00'.format(dateFrom.strftime('%Y-%m-%d')),
							 '--until', '{0} 00:00:00'.format(dateTo.strftime('%Y-%m-%d')), '--pretty=BEGIN%n%cn%n-----%n%s%n-----%n%b%nEND', '--reverse'],
		stdout = subprocess.PIPE, stderr = subprocess.PIPE, cwd = GIT_ROOT)
	return washLogText(proc.stdout, dateFrom)

def washLogText(reader, dateFrom):
	log_ = {"bug" 		: {"log" : "", "idx" : 0},
			"demand" 	: {"log" : "", "idx" : 0},
			"optimize"  : {"log" : "", "idx" : 0},
			"other"  	: {"log" : "", "idx" : 0},
			"merge"  	: {"log" : "", "idx" : 0},}

	log = {'commiter' : '', 'title' : '', 'content' : ''}
	log_set = []
	current_state = ''

	for line in reader.readlines():
		if(line.strip()==""):
			continue
		if(line.strip().find('Change-Id:')==0):
			continue
		line = line.decode('utf-8').encode('gbk')

	 	if(line[:5]=='BEGIN'):
	 		current_state = 'commiter'
	 		log['commiter'] = ''
	 		log['title'] = ''
	 		log['content'] = ''
	 		continue
	 	if(line[:5]=='-----' and current_state=='commiter'):
			current_state = 'title'
			continue
		if(line[:5]=='-----' and current_state=='title'):
			current_state = 'content'
			continue
		if(line[:3]=='END' and current_state=='content'):
			log_set.append(log.copy())
			continue

		log[current_state] += line
		
	for item in log_set:
		type = ''
		if item['title'][0].strip('[【')=='':
			type = item['title']
		elif item['content'][0].strip('[【')=='':
			type = item['content']

		if type.strip().lower()[1:].startswith('bug'):
			type = 'bug'
		elif type.strip().lower()[1:].startswith('d'):
			type = 'demand'
		elif type.strip().lower()[1:].startswith('opt'):
			type = 'optimize'
		elif type.strip().lower()[1:].startswith('other'):
			type = 'other'
		elif type.strip().lower()[1:].startswith('m'):
			type = 'merge'
		else:
			type = 'other'

		title = ''
		if len(item['content'])!=0:
			title = item['content']
		else:
			title = item['title']
		log_[type]["idx"] += 1
		log_[type]["log"] += '{0}、{1} - {2}\r\n'.format(log_[type]["idx"], title.strip('\r\n'), item['commiter'])

	log_count = reduce(lambda x,y : x + y[1]["idx"], log_.iteritems(), 0)
	if log_count==0:
	 	sys.exit(0)

	return "需求：\r\n{0}\r\n修复Bug：\r\n{1}\r\n优化：\r\n{2}\r\n合并：\r\n{3}\r\n其它：\r\n{4}\r\n".format(log_["demand"]["log"], log_["bug"]["log"], log_["optimize"]["log"], log_["merge"]["log"], log_["other"]["log"])

def getLatestApk():
	xmlRoot = xml.etree.ElementTree.parse(os.path.join(JOB_BUILDS_PATH, BUILD_HISTORY_FILE)).getroot()
	last_successful_id = xmlRoot.find('markers').find('LAST__SUCCESSFUL').text
	for build in xmlRoot.find('builds').findall('build'):
		build_number = build.find('number').text
		if build_number!=last_successful_id:
			continue
		build_dir = build.find('buildDir').text
		build_dir = build_dir.replace('/', '\\')
		build_dir = os.path.join(JOB_BUILDS_PATH, build_dir)
		release_apk = os.path.join(build_dir, 'archive\\MSearch\\release\\MSearch_{0}_Release.apk'.format(build_number))
		debug_apk = os.path.join(build_dir, 'archive\\MSearch\\release\\MSearch_{0}_Debug.apk'.format(build_number))
		mapping_file = os.path.join(build_dir, 'archive\\MSearch\\release\\mapping_{0}.txt'.format(build_number))

		return build_number, release_apk, debug_apk, mapping_file

def addAttach(msg, filename):
	ctype,encoding = mimetypes.guess_type(filename)
	if ctype is None or encoding is not None:
	    ctype='application/octet-stream'
	maintype,subtype = ctype.split('/',1)

	att=MIMEImage(open(filename, 'rb').read(),subtype)
	att["Content-Disposition"] = 'attachmemt;filename="{0}"'.format(os.path.basename(filename))
	msg.attach(att)

def sendMail(gitLog, dateFrom, build_number, ziped_file):
	msg=MIMEMultipart()
	#mailTo = ["liumingbin@360.cn"]
	mailTo = ["g-360mso-app@360.cn", "merlin@360.cn", "gaoqingguang@360.cn", "chenzhuo-b@360.cn", "yujun@360.cn", "wangtianping@360.cn", "tianxinchao@360.cn", "wuliang-b@360.cn", "zhangle-s@360.cn", "guoxiaolong-browser@360.cn"]
	msg["From"] = "msoapp_hudson@360.cn"
	msg["To"] = ','.join(mailTo)
	msg["Subject"] = "{2}提测 #{0} @{1}".format(build_number, dateFrom.strftime('%Y-%m-%d %H:%M'), MAIL_TITLE)

	text=MIMEText(gitLog, 'plain', 'gb2312')
	msg.attach(text)

	addAttach(msg, ziped_file)

	smtp=smtplib.SMTP()
	smtp.connect("mail.corp.qihoo.net")
	smtp.sendmail(msg["From"], mailTo, msg.as_string())

	smtp.quit()

	return

def zipApks(build_number, release_apk, debug_apk, mapping_file):
	zipApk = 'D:\MSearch_{0}_Package.7z'.format(build_number)
	subprocess.Popen(['7z.exe', 'a', zipApk, release_apk, debug_apk, mapping_file]).wait()
	return zipApk

def main():
	dateFrom = datetime.datetime.today()
	dateTo = dateFrom + datetime.timedelta(days=1)

	gitLog = getGITLog(dateFrom, dateTo)
	gitLog += '\r\n\r\n[此邮件由系统自动生成发送，请勿回复。]'

	build_number, release_apk, debug_apk, mapping_file = getLatestApk()
	ziped_file = zipApks(build_number, release_apk, debug_apk, mapping_file)
	sendMail(gitLog, dateFrom, build_number, ziped_file)
	os.remove(ziped_file)
	return

if __name__ == '__main__':
	main()
