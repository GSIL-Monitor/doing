#include <string.h>
#include <jni.h>
#include <unistd.h>
#include <sys/types.h> 
#include <sys/stat.h>
#include <fcntl.h>
#include <android/log.h>
#include <errno.h>
#include <sys/mman.h>
#include <time.h>
#include <stdlib.h>
#include <stdio.h>
#include <pthread.h>


#include "config.h"

#define LOG_MODULE_TAG "LOG_SYSTEM"


#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_MODULE_TAG, __VA_ARGS__) 
#ifdef DEBUG
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG , LOG_MODULE_TAG, __VA_ARGS__)
#else
#define LOGD(...)
#endif
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO  , LOG_MODULE_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN  , LOG_MODULE_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , LOG_MODULE_TAG, __VA_ARGS__)

#define MAX_PATH 256

//日志级别
int g_debug_level = LEVEL_ERROR;

//单个日志文件大小
int g_max_size = 0;

//是否输出到logcat
bool g_use_logcat = false;

int g_cur_log_file_fd = -1;
char *g_cur_log_file_map = NULL;
char g_cur_log_file_path[MAX_PATH];
int g_cur_log_file_size = 0;


#ifdef LOG_MODULE 
#ifdef ROOT_LOG_LEVEL

static pthread_mutex_t log_writing_lock = PTHREAD_MUTEX_INITIALIZER; 

bool switch_log_file(char *logDir);

//创建多级目录
int mkdirs(const char *sPathName) 
{
	char DirName[256]; 
	strcpy(DirName, sPathName); 
	int i,len = strlen(DirName); 
	if(DirName[len-1]!='/')
		strcat(DirName,"/"); 

	len = strlen(DirName); 

	for(i=1; i<len; i++) 
	{
		if(DirName[i]=='/')
		{
			DirName[i] = 0; 
			if( access(DirName, 0)!=0 ) 
			{
				if(mkdir(DirName, 0755)==-1) 
				{
					LOGE("mkdir error ,%d,%s",errno,strerror(errno)); 
					return -1; 
				}
			}
			DirName[i] = '/'; 
		}
	}

	return 0; 
}

//jstring 转换为 char*
//获得的指针需要在外部用delete释放
char* jstringTostring(JNIEnv* env, jstring jstr)
{
	char* rtn = NULL;
	jclass clsstring = env->FindClass("java/lang/String");
	jstring strencode = env->NewStringUTF("utf-8");
	jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
	jbyteArray barr= (jbyteArray)env->CallObjectMethod(jstr, mid, strencode);
	jsize alen = env->GetArrayLength(barr);
	jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
	
	rtn = (char*)new char[alen + 1];
	if (alen > 0)
	{
		memcpy(rtn, ba, alen);
	}
	rtn[alen] = 0;
	
	env->ReleaseByteArrayElements(barr, ba, 0);
	return rtn;
}


bool set_out_file(const char* path){
	if(g_cur_log_file_fd < 0){
		LOGD("setOutFile:: close the log file");
		close(g_cur_log_file_fd);
	}

	if(path != NULL)
	{
		int fd = open(path,O_RDWR | O_CREAT | O_APPEND);
			
		if(fd >= 0)
		{
			g_cur_log_file_fd = fd;
			LOGD("setOutFile:: use file [%s]",path);
			return true;
		}
		else
		{
			LOGE("setOutFile:: log file [%s] open fail,%d,%s",path,errno,strerror(errno));
			return false;
		}
	}

	return false;
}

extern "C" {


JNIEXPORT jboolean JNICALL Java_com_qihoo_haosou_msearchpublic_util_Log_nativeInit( JNIEnv* env,jobject thiz,jboolean useLogcat,jint debugLevel,jint maxSize)
{

	g_debug_level = debugLevel;
	g_max_size = maxSize;
	g_use_logcat = useLogcat;

	return true;

}


JNIEXPORT jboolean JNICALL Java_com_qihoo_haosou_msearchpublic_util_Log_nativeSetOutFile(JNIEnv* env,jobject thiz,jstring jOutFile)
{
	LOGD("nativeInit:: jOutFile=[%d]",(int)jOutFile);
	if(g_cur_log_file_fd >= 0){
		LOGD("nativeSetOutFile:: close the log file");
		close(g_cur_log_file_fd);
		g_cur_log_file_fd = -1;
	}

	bool succ = false;
	if(jOutFile != NULL)
	{
		char *logFile = jstringTostring(env,jOutFile);
		succ = set_out_file(logFile);
		if(succ){
			strcpy(g_cur_log_file_path,logFile);
		}
		delete logFile;
	}

	return succ;
}


/*

bool switch_log_file(char *logDir)
{
	if(g_cur_log_file_map)
	{
		munmap(g_cur_log_file_map);
	}

	if(g_cur_log_file_fd >= 0)
	{
		close(g_cur_log_file_fd);
	}

	for(int i=0;i<LOG_MAX_FILE_COUNT;i++)
	{
		char file[MAX_PATH];
		sprintf(file,"%s/%d.log",logDir,i);

		if(access(file,F_OK)!=-1)
		{
			LOGD("switch_log_file:: use file [%s]",file);
			int fd = open(file,O_RDWR);
			if(fd >= 0)
			{
				char *ptr = mmap(NULL,len,PROT_READ|PROT_WRITE,MAP_SHARED,fd,0);
				if(ptr != MAP_FAILED && ptr){
					g_cur_log_file_fd = fd;
					g_cur_log_file_map = ptr;
					g_cur_log_file_size = 0;
					return true;
				}
				else
				{
					LOGE("log file [%s] mmap error,%d,%s",file,errno,strerror(errno));
					close(fd);
				}
			}
			else
			{
				LOGD("switch_log_file:: open file [%s] fail,%d,%s",file,errno,strerror(errno));
			}
			return false;
			
		}
	}
}*/


JNIEXPORT void JNICALL Java_com_qihoo_haosou_msearchpublic_util_Log_nativeSetDebugLevel( JNIEnv* env,jobject thiz,jint level)
{
	g_debug_level = level;
}

void inline format_msg(char* buf,const char* tag,const char *level,char *msg){
	time_t now;
    struct tm *tm_now;

    time(&now);
    tm_now = localtime(&now);

	sprintf(buf,"%04d-%02d-%02d %02d:%02d:%02d %s %s %s",tm_now->tm_year+1900, tm_now->tm_mon+1, tm_now->tm_mday, tm_now->tm_hour, tm_now->tm_min, tm_now->tm_sec,level,tag,
		msg);
}

JNIEXPORT void JNICALL Java_com_qihoo_haosou_msearchpublic_util_Log_nativeLog( JNIEnv* env,jobject thiz,jint level,jstring jtag,jstring jmsg)
{
	if(level >= g_debug_level)
	{
		char *str = new char[1]{0};
		char *tag = new char[1]{0};
		
		if(jmsg != NULL)
			str = jstringTostring(env,jmsg);
		
		if(jtag != NULL)
			tag = jstringTostring(env,jtag);
		
		int msg_size = strlen(str);
		char *buf = new char[msg_size+256];

		switch(level){
			case LEVEL_VERBOSE:
				format_msg(buf,tag,"VERBOSE",str);
				if(g_use_logcat)
					__android_log_print(ANDROID_LOG_VERBOSE , tag,"%s", str);
				break;
			case LEVEL_DEBUG:
				format_msg(buf,tag,"DEBUG",str);
				if(g_use_logcat)
					__android_log_print(ANDROID_LOG_DEBUG , tag,"%s", str);
				break;
			case LEVEL_INFO:
				format_msg(buf,tag,"INFO",str);
				if(g_use_logcat)
					__android_log_print(ANDROID_LOG_INFO , tag,"%s", str);
				break;
			case LEVEL_WARN:
				format_msg(buf,tag,"WARN",str);
				if(g_use_logcat)
					__android_log_print(ANDROID_LOG_WARN , tag,"%s", str);
				break;
			case LEVEL_ERROR:
				format_msg(buf,tag,"ERROR",str);
				if(g_use_logcat)
					__android_log_print(ANDROID_LOG_ERROR , tag,"%s", str);
				break;
		}
	
		int len = strlen(buf);
		buf[len] = '\n';
		buf[len+1] = 0;
	//	memcpy(g_cur_log_file_map,buf,len);
		if(g_cur_log_file_fd >= 0)
		{
			pthread_mutex_lock(&log_writing_lock);
			if((access(g_cur_log_file_path,F_OK))==-1) {
				LOGD("nativeLog:: file is not found,call set_out_file");
				set_out_file(g_cur_log_file_path);
			}
			write(g_cur_log_file_fd,buf,len+1);
			pthread_mutex_unlock(&log_writing_lock);
		}
	//	g_cur_log_file_map += len;
	//	*g_cur_log_file_map = '\n';
	//	g_cur_log_file_map++;
		delete str;
		delete tag;
	}
}


JNIEXPORT void JNICALL Java_com_qihoo_haosou_msearchpublic_util_Log_nativeClose(JNIEnv* env,jobject thiz)
{
		if(g_cur_log_file_fd >= 0)
		{
			close(g_cur_log_file_fd);
			g_cur_log_file_fd = -1;
		}
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved)
{
	
	return JNI_VERSION_1_4;
}

}

#else

extern "C" {
jboolean Java_com_qihoo_haosou_msearchpublic_util_Log_native_init( JNIEnv* env,jobject thiz,bool useLogcat,jstring jlogFile,jint debugLevel,jint maxSize);
}

#endif //ROOT_LOG_LEVEL
#endif //LOG_MODULE
