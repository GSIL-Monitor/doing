#!/usr/bin/env python
#coding:utf-8

import sys
import os.path
import shutil
import subprocess
import time
from multiprocessing import Pool

AAPT = '/Users/liumingbin/Workspace/Android/android-sdk/tools/aapt'
JARSIGNER = '/usr/bin/jarsigner'
ZIPALIGN = '/Users/liumingbin/Workspace/Android/android-sdk/build-tools/20.0.0/zipalign'
KEYSTORE = '/Users/liumingbin/Workspace/360/msearch/git-svn/trunk/keystore'
KEYALIAS = 'qihoo'
KEYPASS = 'stchou@360123456'
CHANNEL_PATH = 'assets/channel'
NUM_PROCESS = 12

def removeOldChannelAssets(apkFile, channel):
	ret = subprocess.Popen([AAPT, 'r', apkFile, CHANNEL_PATH], stdout = subprocess.PIPE, stderr = subprocess.PIPE).wait()
	if ret!=0:
		raise ValueError("[{0}]:Error: 'aapt r assets/channel' failed!".format(channel))

def generateNewChannelAssets(apkFile, channel):
	rootPath = os.path.dirname(apkFile)
	channelAssetsPath = os.path.join(rootPath, 'assets')
	os.mkdir(channelAssetsPath)
	if not os.path.exists(channelAssetsPath):
		raise ValueError("[{0}]:Error: mkdir assets failed!".format(channel))

	lineChannel = "MSEARCH_CHANNEL={0}".format(channel)
	lineBuildTime = "BUILD_TIME={0}".format(time.strftime('%Y/%m/%d %H:%M'))

	with open(os.path.join(channelAssetsPath, 'channel'), 'w') as channelFile:
		channelFile.write(lineChannel)
		channelFile.write('\n')
		channelFile.write(lineBuildTime)

def addNewChannelAssets(apkFile, channel):
	ret = subprocess.Popen([AAPT, 'a', apkFile, CHANNEL_PATH], cwd=os.path.dirname(apkFile), stdout = subprocess.PIPE, stderr = subprocess.PIPE).wait()
	if ret!=0:
		raise ValueError("[{0}]:Error: 'aapt a assets/channel' failed!".format(channel))

def removeAssetsDir(apkFile, channel):
	shutil.rmtree(os.path.join(os.path.dirname(apkFile), 'assets'))

def signApk(apkFile, channel):
	filename, extname = os.path.splitext(apkFile)
	signedApk = filename + '_signed' + extname

	ret = subprocess.Popen([JARSIGNER, '-keystore', KEYSTORE, '-storepass', KEYPASS, '-keypass', KEYPASS, '-digestalg', 'SHA1', '-sigalg', 'SHA1withRSA', '-sigFile', 'CERT', '-signedjar', signedApk, apkFile, KEYALIAS], stdout = subprocess.PIPE, stderr = subprocess.PIPE).wait()
	if ret!=0:
		raise ValueError("[{0}]:Error: jarsigner failed!".format(channel))

	return signedApk

def zipAlign(signedApk, channel):
	filename, extname = os.path.splitext(signedApk)
	alignedApk = filename + '_aligned' + extname

	ret = subprocess.Popen([ZIPALIGN, '-v', '4', signedApk, alignedApk], stdout = subprocess.PIPE, stderr = subprocess.PIPE).wait()
	if ret!=0:
		raise ValueError("[{0}]:Error: zipalign failed!".format(channel))

	return alignedApk

def renameApk(apkFile, signedApk, alignedApk, channel):
	filename, extname = os.path.splitext(apkFile)
	channelApk = filename + '_{0}'.format(channel) + extname

	os.remove(apkFile)
	os.remove(signedApk)
	os.rename(alignedApk, channelApk)

	return channelApk

def copyChannelApk(channelApk, rootDir):
	shutil.copy(channelApk, rootDir)

def removeChannelDir(apkFile):
	shutil.rmtree(os.path.dirname(apkFile))

def repackApkInternal(rootDir, apkFile, channel):
	try:
		print("[{0}]: Repack apk for channel '{0}'...".format(channel))
		removeOldChannelAssets(apkFile, channel)
		generateNewChannelAssets(apkFile, channel)
		addNewChannelAssets(apkFile, channel)
		removeAssetsDir(apkFile, channel)
		signedApk = signApk(apkFile, channel)
		alignedApk = zipAlign(signedApk, channel)
		channelApk = renameApk(apkFile, signedApk, alignedApk, channel)
		copyChannelApk(channelApk, rootDir)
		print("[{0}]: Repack apk for channel '{0}'' succeed!".format(channel))
	except Exception as e:
		print e
		with open('repack_errorlog.txt', 'a') as f:
			f.write('[{0}]{1}\n'.format(time.strftime('%Y/%m/%d %H:%M:%S'), e))
	finally:
		removeChannelDir(apkFile)

def repackApk(apkFile, channel):
	if not os.path.exists(channel):
		os.mkdir(channel)

	if not os.path.isdir(channel):
		print("[{0}]:Error: mkdir {0} failed!".format(channel))
		return

	rootDir = os.path.dirname(os.path.abspath(apkFile))
	shutil.copy(apkFile, channel)
	repackApkInternal(rootDir, os.path.join(os.path.abspath(channel), os.path.basename(apkFile)), channel)

def parseArgs():
	if len(sys.argv) < 3:
		print("Usage: python channel_repack.py apkFilePath channelListFile")
		sys.exit(1)

	apkFilePath = sys.argv[1]
	channelListFilePath = sys.argv[2]

	if not os.path.isfile(apkFilePath):
		print("Error: {0} not found!".format(apkFilePath))
		sys.exit(1)

	if not os.path.isfile(channelListFilePath):
		print("Error: {0} not found!".format(channelListFilePath))
		sys.exit(1)

	return apkFilePath, channelListFilePath

def main():
	apkFilePath, channelListFilePath = parseArgs();

	workerPool = Pool(processes=NUM_PROCESS)
	with open(channelListFilePath, 'r') as f:
		for channel in f.readlines():
			channel = channel.strip()
			if len(channel)==0:
				continue

			workerPool.apply_async(repackApk, (apkFilePath, channel))

		workerPool.close()
		workerPool.join()

if __name__ == '__main__':
	main()
