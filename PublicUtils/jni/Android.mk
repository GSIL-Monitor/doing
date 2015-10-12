# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := logutil
LOCAL_SRC_FILES := log_for_android.cpp
LOCAL_LDLIBS := -llog     

LOCAL_SHARED_LIBRARIES := liblog libcutils

include $(BUILD_SHARED_LIBRARY)


#---------------------------------------------

include $(CLEAR_VARS)

LOCAL_MODULE    := watcher

#LOCAL_LDFLAGS += $(LOCAL_PATH)/libcurl.a
#LOCAL_STATIC_LIBRARIES += $(LOCAL_PATH)/libcurl.a

LOCAL_SRC_FILES := \
		uninstall_watcher.cpp \

LOCAL_CFLAGS += -pie -fPIE
LOCAL_LDFLAGS += -pie -fPIE

LOCAL_LDLIBS    +=  -llog  
LOCAL_SHARED_LIBRARIES := liblog libcutils
    
include $(BUILD_EXECUTABLE)

#---------------------------------------------