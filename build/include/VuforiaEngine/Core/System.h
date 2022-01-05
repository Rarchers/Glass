/*===============================================================================
Copyright (c) 2021 PTC Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other
countries.
===============================================================================*/

#ifndef _VU_SYSTEM_H_
#define _VU_SYSTEM_H_

/**
 * \file System.h
 * \brief Platform-specific macros and data types
 */

// Get platform-specific defines
#if defined(WIN32) || defined(_WIN32)
#   define VU_IS_WINDOWS
#   if defined(WINAPI_FAMILY) && (WINAPI_FAMILY == WINAPI_FAMILY_PC_APP)
#       define VU_PLATFORM_UWP
#   else
#       define VU_PLATFORM_WINDOWS
#   endif
// The Lumin toolchain doesn't provide any defines to allow it to be uniquely identified, it does define __ANDROID__
// So we can identify Lumin from Android, Lumin developers must define VU_IS_LUMIN in their mabu/project file.
#elif defined (VU_IS_LUMIN)
#   define VU_PLATFORM_LUMIN
#elif defined (__ANDROID__)
#   define VU_PLATFORM_ANDROID
#elif defined (__APPLE__)
#   include <TargetConditionals.h>
#   if defined (__arm__) || defined (__thumb__) || defined (__arm64__) || (defined(TARGET_OS_SIMULATOR) && TARGET_OS_SIMULATOR)
#       define VU_PLATFORM_IOS
#   else
#       define VU_PLATFORM_MACOS
#   endif
#elif defined (__linux__)
#   define VU_PLATFORM_LINUX
#else
#   error "Unknown platform"
#endif

// Define exporting / importing of methods from module
#ifdef VU_IS_WINDOWS
#   ifdef VU_EXPORTS
#       define VU_API __declspec(dllexport)
#   elif defined(VU_STATIC)
#       define VU_API
#   else
#       define VU_API __declspec(dllimport)
#   endif
#else // !VU_IS_WINDOWS
#   ifdef VU_EXPORTS
#       define VU_API __attribute__((visibility("default")))
#   elif defined(VU_STATIC)
#       define VU_API
#   else
#       define VU_API __attribute__((visibility("default")))
#   endif
#endif // VU_IS_WINDOWS

// Define calling conventions
#ifdef VU_IS_WINDOWS
#   define VU_API_CALL __stdcall
#else // !VU_IS_WINDOWS
#   define VU_API_CALL
#endif // VU_IS_WINDOWS

// Cross-platform macro for storing the value of the C++ language standard
// targeted by the compiler as an integer literal
/**
 * \note The MSVC compiler does not conform to the C++ standard regarding the __cplusplus macro.
 * Unless the /Zc:__cplusplus compiler option is enabled in Visual Studio 2017 v15.7 or above,
 * the __cplusplus macro always reports 199711L (C++98). The _MSVC_LANG macro stores the expected
 * C++ language standard value in Visual Studio 2015 Update 3 and above.
 */
#ifndef VU_CPLUSPLUS
#    if defined(_MSVC_LANG) && !defined(__clang__)
#        define VU_CPLUSPLUS (_MSC_VER == 1900 ? 199711L : _MSVC_LANG)
#    elif defined(__cplusplus)
#        define VU_CPLUSPLUS __cplusplus
#    endif // _MSVC_LANG && !__clang__
#endif

// Macro for defining enums
/**
 * \note In order to enforce the size of the enum type for ABI compatibility, use the underlying
 * type feature of enums if the compiler is targeting C++11 and above, otherwise use int32_t for
 * fixed size.
 */
#if VU_CPLUSPLUS && VU_CPLUSPLUS >= 201103L
#   define VU_ENUM(_type) enum _type : int32_t
#else
#   define VU_ENUM(_type) \
    typedef int32_t _type; \
    enum
#endif // VU_CPLUSPLUS && VU_CPLUSPLUS >= 201103L

// Macro for defining a compile-time integer constant
#define VU_CONST_INT(name, value) enum \
{ \
    name = value \
}

#endif // _VU_SYSTEM_H_
