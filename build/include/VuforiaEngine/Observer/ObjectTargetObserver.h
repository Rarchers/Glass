/*===============================================================================
Copyright (c) 2021 PTC Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other
countries.
===============================================================================*/

#ifndef _VU_OBJECTTARGETOBSERVER_H_
#define _VU_OBJECTTARGETOBSERVER_H_

/**
 * \file ObjectTargetObserver.h
 * \brief Observer for the Object Target feature
 */

#include <VuforiaEngine/Engine/Engine.h>

#ifdef __cplusplus
extern "C"
{
#endif

/** \addtogroup ObjectTargetObserverGroup Object Target Feature
 * \{
 */

 /// \brief Configuration error for Cylinder Target creation
VU_ENUM(VuObjectTargetCreationError)
{
    VU_OBJECT_TARGET_CREATION_ERROR_NONE                  = 0x0, ///< No error
    VU_OBJECT_TARGET_CREATION_ERROR_INTERNAL              = 0x1, ///< Observer auto-activation failed
    VU_OBJECT_TARGET_CREATION_ERROR_AUTOACTIVATION_FAILED = 0x2, ///< An error occurred while auto-activating the observer
    VU_OBJECT_TARGET_CREATION_ERROR_DATABASE_LOAD_ERROR   = 0x3, ///< Database file not found or an error occurred when reading data from it
                                                                 ///< (potentially unknown or corrupted file)
    VU_OBJECT_TARGET_CREATION_ERROR_INVALID_TARGET_NAME   = 0x4, ///< Invalid target name
    VU_OBJECT_TARGET_CREATION_ERROR_TARGET_NOT_FOUND      = 0x5, ///< Specified target not found in database
    VU_OBJECT_TARGET_CREATION_ERROR_INVALID_SCALE         = 0x6  ///< Invalid value passed to the scale parameter
};


/// \brief Configuration for creating an Object Target observer
typedef struct VuObjectTargetConfig
{
    /// \brief Path to database containing targets
    const char* databasePath;

    /// \brief Target name
    const char* targetName;

    /// \brief Observer activation
    /**
     * \note The default value is VU_TRUE.
     */
    VuBool activate;

    /// \brief Scale multiplication factor
    /**
     * \note The default value is 1.0f.
     */
    float scale;

    /// \brief Offset from the origin of the target to the pose reported by an observation, relative to the target's frame of reference
    /**
     * \note The default value is identity matrix.
     */
    VuMatrix44F poseOffset;
} VuObjectTargetConfig;

/// \brief Default Object Target configuration
/**
 * \note Use this function to initialize the VuObjectTargetConfig data structure with default values.
 */
VU_API VuObjectTargetConfig VU_API_CALL vuObjectTargetConfigDefault();

/// \brief Target info for an Object Target from its respective observation
typedef struct VuObjectTargetObservationTargetInfo
{
    /// \brief Persistent system-wide unique ID associated with the Object Target
    /**
     * \note The unique ID can't be changed.
     */
    const char* uniqueId;
    
    /// \brief Target name
    const char* name;
    
    /// \brief Size (dimensions) of the Object Target in meters
    VuVector3F size;
    
    /// \brief Axis-aligned bounding box of the observed target, relative to the target's frame of reference
    VuAABB bbox;

    /// \brief Pose offset used with the Object Target
    VuMatrix44F poseOffset;
} VuObjectTargetObservationTargetInfo;

/// \brief status info for Object Target observation
VU_ENUM(VuObjectTargetObservationStatusInfo)
{
    VU_OBJECT_TARGET_OBSERVATION_STATUS_INFO_NORMAL       = 0x1, ///< Tracking working normally
    VU_OBJECT_TARGET_OBSERVATION_STATUS_INFO_NOT_OBSERVED = 0x2, ///< Target is not detected
    VU_OBJECT_TARGET_OBSERVATION_STATUS_INFO_RELOCALIZING = 0x3  ///< The tracking system is currently relocalizing
};

/// \brief Type identifier for Object Target observers
VU_CONST_INT(VU_OBSERVER_OBJECT_TARGET_TYPE, 0x5);

/// \brief Type identifier for Object Target observations
VU_CONST_INT(VU_OBSERVATION_OBJECT_TARGET_TYPE, 0x5);

/// \brief Create an Object Target observer from a database
VU_API VuResult VU_API_CALL vuEngineCreateObjectTargetObserver(VuEngine* engine, VuObserver** observer, const VuObjectTargetConfig* config, VuObjectTargetCreationError* error);

/// \brief Get all Object Target observers
VU_API VuResult VU_API_CALL vuEngineGetObjectTargetObservers(const VuEngine* engine, VuObserverList* observerList);

/// \brief Get the unique ID associated to the target from an Object Target observer
VU_API VuResult VU_API_CALL vuObjectTargetObserverGetTargetUniqueId(const VuObserver* observer, const char** targetId);

/// \brief Get the name associated to the target from an Object Target observer
VU_API VuResult VU_API_CALL vuObjectTargetObserverGetTargetName(const VuObserver* observer, const char** targetName);

/// \brief Get the size in meters associated to the target from an Object Target observer
VU_API VuResult VU_API_CALL vuObjectTargetObserverGetTargetSize(const VuObserver* observer, VuVector3F* size);

/// \brief Get the axis-aligned bounding box associated to the target from an Object Target observer, relative to the target's frame of reference
VU_API VuResult VU_API_CALL vuObjectTargetObserverGetAABB(const VuObserver* observer, VuAABB* bbox);

/// \brief Re-scale the target size associated to an Object Target observer
VU_API VuResult VU_API_CALL vuObjectTargetObserverSetTargetScale(VuObserver* observer, float scale);

/// \brief Get the pose transformation offset associated to the target from an Object Target observer
VU_API VuResult VU_API_CALL vuObjectTargetObserverGetTargetPoseOffset(const VuObserver* observer, VuMatrix44F* poseOffset);

/// \brief Get the pose transformation offset associated to the target from an Object Target observer
VU_API VuResult VU_API_CALL vuObjectTargetObserverSetTargetPoseOffset(VuObserver* observer, const VuMatrix44F* poseOffset);

/// \brief Get the motion hint associated to the target from a Object Target observer
VU_API VuResult VU_API_CALL vuObjectTargetObserverGetMotionHint(const VuObserver* observer, VuMotionHint* motionHint);

/// \brief Set the motion hint associated to the target from a Object Target observer
/**
 * \note This operation will reset any tracking operation for a Object Target observer
 * (recommended to use this method before starting Vuforia engine)
 *
 * \note Only VU_MOTION_HINT_ADAPTIVE and VU_MOTION_HINT_DYNAMIC are allowed
 */
VU_API VuResult VU_API_CALL vuObjectTargetObserverSetMotionHint(VuObserver* observer, VuMotionHint motionHint);

/// \brief Set the maximum number of Object Targets tracked at the same time
/**
 * \note This setting tells Vuforia how many object trargets shall be processed at most at the same
 * time. This value is clamped at a maximum of 2. The default value is 1.
 */
VU_API VuResult VU_API_CALL vuEngineSetMaximumSimultaneousObjectTargets(VuEngine* engine, int32_t maxNumberOfTargets);

/// \brief Get the maximum number of Object Targets tracked at the same time
VU_API VuResult VU_API_CALL vuEngineGetMaximumSimultaneousObjectTargets(const VuEngine* engine, int32_t* maxNumberOfTargets);

/// \brief Set delayed loading for all Object Targets
/**
 * This setting tells Vuforia to enable delayed loading of Object Target databases
 * upon first detection. Loading time of large object database will be reduced
 * but the initial detection time of targets will increase. Please note that the
 * setting should be set before loading any Object Target database to be effective.
 * The default value is VU_FALSE.
 *
 * \note This can only be set when Vuforia is not running
 */
VU_API VuResult VU_API_CALL vuEngineSetObjectTargetsDelayedLoading(VuEngine* engine, VuBool delayedLoading);

/// \brief Get current setting for delayed loading for all Object Targets
VU_API VuResult VU_API_CALL vuEngineGetObjectTargetsDelayedLoading(const VuEngine* engine, VuBool* delayedLoading);

/// \brief Get all Object Target observations
VU_API VuResult VU_API_CALL vuStateGetObjectTargetObservations(const VuState* engine, VuObservationList* observationList);

/// \brief Get status info associated with an Object Target observation
VU_API VuResult VU_API_CALL vuObjectTargetObservationGetStatusInfo(const VuObservation* observation, VuObjectTargetObservationStatusInfo* statusInfo);

/// \brief Get target info associated with an Object Target observation
VU_API VuResult VU_API_CALL vuObjectTargetObservationGetTargetInfo(const VuObservation* observation, VuObjectTargetObservationTargetInfo* targetInfo);

/** \} */

#ifdef __cplusplus
}
#endif

#endif // _VU_OBJECTTARGETOBSERVER_H_
