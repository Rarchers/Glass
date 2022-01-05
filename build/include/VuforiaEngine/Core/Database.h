/*===============================================================================
Copyright (c) 2021 PTC Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other
countries.
===============================================================================*/

#ifndef _VU_DATABASE_H_
#define _VU_DATABASE_H_

/**
 * \file Database.h
 * \brief Functionality supporting the discovery of target information in databases
 */

#include <VuforiaEngine/Engine/Engine.h>

#ifdef __cplusplus
extern "C"
{
#endif

/** \addtogroup DatabaseTargetInfoGroup Database Target info
 * \{
 * Utilities to extract information on all targets from a database from which observers can be created.
 */

/// \brief Info for a target in a database
typedef struct VuDatabaseTargetInfo
{
    /// \brief Observer type that should be used with the target
    VuObserverType observerType;

    /// \brief Target name
    /// \note The lifetime of this pointer is bound to the enclosing VuDatabaseTargetInfoList
    const char* name;
} VuDatabaseTargetInfo;

/// \brief List for holding information about targets in database
typedef struct VuDatabaseTargetInfoList_ VuDatabaseTargetInfoList;

/// \brief Create a database target info list
VU_API VuResult VU_API_CALL vuDatabaseTargetInfoListCreate(VuDatabaseTargetInfoList** list);

/// \brief Get number of elements in database target info list
VU_API VuResult VU_API_CALL vuDatabaseTargetInfoListGetSize(const VuDatabaseTargetInfoList* list, int32_t* listSize);

/// \brief Get an element in a database target info list
VU_API VuResult VU_API_CALL vuDatabaseTargetInfoListGetElement(const VuDatabaseTargetInfoList* list, int32_t element, VuDatabaseTargetInfo* targetInfo);

/// \brief Destroy a database target info list
VU_API VuResult VU_API_CALL vuDatabaseTargetInfoListDestroy(VuDatabaseTargetInfoList* list);

/// Database target info errors
VU_ENUM(VuDatabaseTargetInfoError)
{
    VU_DATABASE_TARGET_INFO_ERROR_NONE                = 0x0, ///< No error
    VU_DATABASE_TARGET_INFO_ERROR_DATABASE_LOAD_ERROR = 0x1, ///< Could not find database file or read data from it
                                                             ///< (potentially unknown or corrupted file)
    VU_DATABASE_TARGET_INFO_ERROR_NO_TARGETS          = 0x2  ///< Could not find any targets in the database from which an observer could be created
};

/// \brief Extract info on all targets from a database from which observers can be created
VU_API VuResult VU_API_CALL vuEngineGetDatabaseTargetInfo(const VuEngine* engine, const char* databasePath, VuDatabaseTargetInfoList* targetInfos, VuDatabaseTargetInfoError* errorCode);

/** \} */

#ifdef __cplusplus
}
#endif

#endif // _VU_DATABASE_H_
