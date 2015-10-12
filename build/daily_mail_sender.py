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

SVN_ROOT = 'D:\\ci-root\\workspace\\msearch-trunk'
JOB_BUILDS_PATH = 'D:\\Hudson\\.hudson\\jobs\\msearch-trunk\\builds'
BUILD_HISTORY_FILE = '_runmap.xml'

def getSVNLog(dateFrom, dateTo):
	proc = subprocess.Popen(['svn', 'log', '-r', '{%s}:{%s}' % (dateFrom.strftime('%Y-%m-%d'), dateTo.strftime('%Y-%m-%d')), SVN_ROOT],
		stdout = subprocess.PIPE, stderr = subprocess.PIPE)
	return washLogText(proc.stdout, dateFrom)

def washLogText(reader, dateFrom):
	revisionLine = False
	findNextSplitLine = False

	log_ = {"bug" 		: {"log" : "", "idx" : 0},
			"demand" 	: {"log" : "", "idx" : 0},
			"optimize"  : {"log" : "", "idx" : 0},
			"other"  	: {"log" : "", "idx" : 0}}

	last_log_type = "other"
	last_commiter = ""

	for line in reader.readlines():
		if(line.strip()==""):
			continue
		if(line.strip('- \t\r\n')==""):
			revisionLine = True
			findNextSplitLine = False
			continue
		if(findNextSplitLine):
			continue
		if(revisionLine):
			revisionLine = False
			if(line.split('|')[2][1:11]!=dateFrom.strftime('%Y-%m-%d')):
				findNextSplitLine = True
			else:
				last_commiter = line.split('|')[1].strip()
			continue
		if line.strip()[0]=='[' or line.strip()[0]=='【':
			if line.strip().lower().startswith("[b"):
				log_["bug"]["idx"] += 1
				log_["bug"]["log"] += '{0}、{1} - {2}\r\n'.format(log_["bug"]["idx"], line.strip('\r\n'), last_commiter)
				last_log_type = "bug"
			elif line.strip().lower().startswith("[d"):
				log_["demand"]["idx"] += 1
				log_["demand"]["log"] += '{0}、{1} - {2}\r\n'.format(log_["demand"]["idx"], line.strip('\r\n'), last_commiter)
				last_log_type = "demand"
			elif line.strip().lower().startswith("[opt"):
				log_["optimize"]["idx"] += 1
				log_["optimize"]["log"] += '{0}、{1} - {2}\r\n'.format(log_["optimize"]["idx"], line.strip('\r\n'), last_commiter)
				last_log_type = "optimize"
			else:
				log_["other"]["idx"] += 1
				log_["other"]["log"] += '{0}、{1} - {2}\r\n'.format(log_["other"]["idx"], line.strip('\r\n'), last_commiter)
				last_log_type = "other"
		else:
			log_[last_log_type]["log"] += '{0}'.format(line)

	log_count = reduce(lambda x,y : x + y[1]["idx"], log_.iteritems(), 0)
	if log_count==0:
		sys.exit(0)

	return "需求：\r\n{0}\r\n修复Bug：\r\n{1}\r\n优化：\r\n{2}\r\n其它：\r\n{3}\r\n".format(log_["demand"]["log"], log_["bug"]["log"], log_["optimize"]["log"], log_["other"]["log"])

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

def sendMail(svnLog, dateFrom, build_number, ziped_file):
	msg=MIMEMultipart()
	#mailTo = ["liumingbin@360.cn", "zhangqiang-s@360.cn"]
	mailTo = ["g-360mso-app@360.cn", "merlin@360.cn", "gaoqingguang@360.cn", "chenzhuo-b@360.cn", "yujun@360.cn", "wangtianping@360.cn", "tianxinchao@360.cn", "wuliang-b@360.cn", "zhangle-s@360.cn", "guoxiaolong-browser@360.cn"]
	msg["From"] = "msoapp_hudson@360.cn"
	msg["To"] = ','.join(mailTo)
	msg["Subject"] = "搜索App提测 #{0} @{1}".format(build_number, dateFrom.strftime('%Y-%m-%d %H:%M'))

	text=MIMEText(svnLog, 'plain', 'gb2312')
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

	svnLog = getSVNLog(dateFrom, dateTo)
	svnLog += '\r\n\r\n[此邮件由系统自动生成发送，请勿回复。]'

	build_number, release_apk, debug_apk, mapping_file = getLatestApk()
	ziped_file = zipApks(build_number, release_apk, debug_apk, mapping_file)
	sendMail(svnLog, dateFrom, build_number, ziped_file)
	os.remove(ziped_file)
	return

if __name__ == '__main__':
	main()
