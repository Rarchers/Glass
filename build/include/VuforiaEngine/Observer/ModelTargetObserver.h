/*===============================================================================
Copyright (c) 2021 PTC Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other
countries.
===============================================================================*/

#ifndef _VU_MODELTARGETOBSERVER_H_
#define _VU_MODELTARGETOBSERVER_H_

/**
 * \file ModelTargetObserver.h
 * \brief Observer for the Model Target feature
 */

#include <VuforiaEngine/Engine/Engine.h>

#ifdef __cplusplus
extern "C"
{
#endif

/** \addtogroup ModelTargetObserverGroup Model Target Feature
 * \{
 */

/// \brief Tracking modes to optimize tracking quality and robustness for different target types
VU_ENUM(VuModelTargetTrackingMode)
{
    VU_MODEL_TARGET_TRACKING_MODE_DEFAULT = 0x1, ///< Model Target tracking parameters optimized to work well on a broad range of object types
    VU_MODEL_TARGET_TRACKING_MODE_CARS    = 0x2, ///< Model Target tracking parameters optimized for car exteriors
    VU_MODEL_TARGET_TRACKING_MODE_SCAN    = 0x3  ///< Model Target tracking parameters optimized for scanned objects
};

/// \brief Configuration error for Model Target creation
VU_ENUM(VuModelTargetCreationError)
{
    VU_MODEL_TARGET_CREATION_ERROR_NONE                    = 0x0, ///< No error
    VU_MODEL_TARGET_CREATION_ERROR_INTERNAL                = 0x1, ///< An internal error occurred while creating the observer
    VU_MODEL_TARGET_CREATION_ERROR_AUTOACTIVATION_FAILED   = 0x2, ///< Observer auto-activation failed
    VU_MODEL_TARGET_CREATION_ERROR_DATABASE_LOAD_ERROR     = 0x3, ///< Database file not found or an error occurred when reading data from it
                                                                  ///< (potentially unknown or corrupted file)
    VU_MODEL_TARGET_CREATION_ERROR_INVALID_TARGET_NAME     = 0x4, ///< Invalid target name
    VU_MODEL_TARGET_CREATION_ERROR_TARGET_NOT_FOUND        = 0x5, ///< Specified target not found in database
    VU_MODEL_TARGET_CREATION_ERROR_INVALID_SCALE           = 0x6, ///< Invalid value passed to the scale parameter
    VU_MODEL_TARGET_CREATION_ERROR_INVALID_GUIDE_VIEW_NAME = 0x7  ///< Invalid value passed to the parameter indicating the default-active Guide View
};

/// \brief Configuration for creating a Model Target observer
typedef struct VuModelTargetConfig
{
    /// \brief Path to database containing targets
    const char* databasePath;

    /// \brief Target name
    const char* targetName;

    /// \brief Name of the Guide View to be active
    /**
     * Set to NULL to keep the default Guide View defined for this Model Target in the database activated
     */
    const char* activeGuideViewName;

    /// \brief Observer activation
    /**
     * \note The default value is VU_TRUE.
     * \note If the Model Target observer was successfully activated, the active guide view's image is generated as well.
     * 
     * \note Model Target observers from different databases cannot be active at the same time. Observer creation will fail if "activate" is
     * set to VU_TRUE while a Model Target observer from another database is active.
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
} VuModelTargetConfig;

/// \brief Default Model Target configuration
/**
 * \note Use this function to initialize the VuModelTargetConfig data structure with default values.
 */
VU_API VuModelTargetConfig VU_API_CALL vuModelTargetConfigDefault();

/// \brief Target info for a Model Target from its respective observation
typedef struct VuModelTargetObservationTargetInfo
{
    /// \brief Persistent system-wide unique ID associated with the Model Target
    /**
     * \note The unique ID can't be changed.
     */
    const char* uniqueId;
    
    /// \brief Target name
    const char* name;
    
    /// \brief Size (dimensions) of the Model Target in meters
    VuVector3F size;
    
    /// \brief Axis-aligned bounding box of the observed Model Target, relative to the target's frame of reference
    VuAABB bbox;
    
    /// \brief Index to the Guide View to be active
    /**
     * Set to NULL if no Guide View is active for this Model Target. This is true for Advanced Model Targets
     * unless a Guide View is recognized which is indicated by a status info of
     * "VU_MODEL_TARGET_OBSERVATION_STATUS_INFO_NO_DETECTION_RECOMMENDING_GUIDANCE".
     * This may also occur if there are multiple Model Targets in a database.
     */
    const char* activeGuideViewName;

    /// \brief Tracking Mode
    VuModelTargetTrackingMode trackingMode;
    
    /// \brief Motion hint
    VuMotionHint motionHint;
    
    /// \brief Pose offset used with the Model Target
    VuMatrix44F poseOffset;

    /// \brief Name of the active Model Target state
    const char* activeState;
} VuModelTargetObservationTargetInfo;

/// \brief status info for Model Target observation
VU_ENUM(VuModelTargetObservationStatusInfo)
{
    VU_MODEL_TARGET_OBSERVATION_STATUS_INFO_NORMAL                             = 0x1, ///< Tracking working normally
    VU_MODEL_TARGET_OBSERVATION_STATUS_INFO_NOT_OBSERVED                       = 0x2, ///< Target is not detected
    VU_MODEL_TARGET_OBSERVATION_STATUS_INFO_INITIALIZING                       = 0x3, ///< The tracking system is currently initializing
    VU_MODEL_TARGET_OBSERVATION_STATUS_INFO_RELOCALIZING                       = 0x4, ///< The tracking system is currently relocalizing
    VU_MODEL_TARGET_OBSERVATION_STATUS_INFO_NO_DETECTION_RECOMMENDING_GUIDANCE = 0x5, ///< Could not snap to the target. Recommend to show a Guide View overlay
    VU_MODEL_TARGET_OBSERVATION_STATUS_INFO_WRONG_SCALE                        = 0x6  ///< The target scale does not match the physical scale of the object
};

/// \brief Type identifier for Model Target observers
VU_CONST_INT(VU_OBSERVER_MODEL_TARGET_TYPE, 0x6);

/// \brief Type identifier for Model Target observations
VU_CONST_INT(VU_OBSERVATION_MODEL_TARGET_TYPE, 0x6);

/// \brief Guide View
typedef struct VuGuideView_ VuGuideView;

/// \brief Guide View List
typedef struct VuGuideViewList_ VuGuideViewList;

/// \brief Create a guide view list
VU_API VuResult VU_API_CALL vuGuideViewListCreate(VuGuideViewList** list);

/// \brief Get the number of elements in the guide view list
VU_API VuResult VU_API_CALL vuGuideViewListGetSize(const VuGuideViewList* list, int32_t* listSize);

/// \brief Get the element at the specified index from the guide view list
VU_API VuResult VU_API_CALL vuGuideViewListGetElement(const VuGuideViewList* list, int32_t element, VuGuideView** guideView);

/// \brief Destroy the guide view list
VU_API VuResult VU_API_CALL vuGuideViewListDestroy(VuGuideViewList* list);

/// \brief Model Target State
typedef struct VuModelTargetState_ VuModelTargetState;

/// \brief Model Target State List
typedef struct VuModelTargetStateList_ VuModelTargetStateList;

/// \brief Create a model target state list
VU_API VuResult VU_API_CALL vuModelTargetStateListCreate(VuModelTargetStateList** list);

/// \brief Destroys a model target state list
VU_API VuResult VU_API_CALL vuModelTargetStateListDestroy(VuModelTargetStateList* list);

/// \brief Get the number of elements in the model target state list
VU_API VuResult VU_API_CALL vuModelTargetStateListGetSize(const VuModelTargetStateList* list, int32_t* listSize);

/// \brief Get the element at the specified index from the model target state list
VU_API VuResult VU_API_CALL vuModelTargetStateListGetElement(const VuModelTargetStateList* list, int32_t element, const VuModelTargetState** state);

/// \brief Create a Model Target observer from database
VU_API VuResult VU_API_CALL vuEngineCreateModelTargetObserver(VuEngine* engine, VuObserver** observer, const VuModelTargetConfig* config, VuModelTargetCreationError* errorCode);

/// \brief Get all Model Target observers
VU_API VuResult VU_API_CALL vuEngineGetModelTargetObservers(const VuEngine* engine, VuObserverList* observerList);

/// \brief Reset tracking of this Model Target observer
/**
 * \note This will stop any ongoing tracking of this Model Target including extended tracking. The tracking will automatically restart if the target is recognized again.
 *
 * \note Only an activated target can be reset.
 */
VU_API VuResult VU_API_CALL vuModelTargetObserverReset(VuObserver* observer);

/// \brief Get the unique ID associated to the target from a Model Target observer
VU_API VuResult VU_API_CALL vuModelTargetObserverGetTargetUniqueId(const VuObserver* observer, const char** targetId);

/// \brief Get the name associated to the target from a Model Target observer
VU_API VuResult VU_API_CALL vuModelTargetObserverGetTargetName(const VuObserver* observer, const char** targetName);

/// \brief Get the size in meters associated to the target from a Model Target observer
VU_API VuResult VU_API_CALL vuModelTargetObserverGetTargetSize(const VuObserver* observer, VuVector3F* size);

/// \brief Re-scale the target size associated to a Model Target observer
VU_API VuResult VU_API_CALL vuModelTargetObserverSetTargetScale(VuObserver* observer, float scale);

/// \brief Get the pose transformation offset associated to the target from a Model Target observer
VU_API VuResult VU_API_CALL vuModelTargetObserverGetTargetPoseOffset(const VuObserver* observer, VuMatrix44F* poseOffset);

/// \brief Get the pose transformation offset associated to the target from a Model Target observer
VU_API VuResult VU_API_CALL vuModelTargetObserverSetTargetPoseOffset(VuObserver* observer, const VuMatrix44F* poseOffset);

/// \brief Set the tracking mode associated to the target from a Model Target observer
/**
 * This setting modifies the internal Model Target tracking parameters to optimize
 * the tracking quality and robustness for different target types.
 *
 * \note Only changes between 'DEFAULT' and 'CAR' are supported.
 *
 * \note This operation will reset any tracking operation for the Model Target observer.
 * It is recommended to use this method before starting the Vuforia Engine.
 */
VU_API VuResult VU_API_CALL vuModelTargetObserverSetTrackingMode(VuObserver* observer, VuModelTargetTrackingMode trackingMode);

/// \brief Get the tracking mode associated to the target from a Model Target observer
VU_API VuResult VU_API_CALL vuModelTargetObserverGetTrackingMode(const VuObserver* observer, VuModelTargetTrackingMode* trackingMode);

/// \brief Set the motion hint associated to the target from a Model Target observer
/**
 * \note This operation will reset any tracking operation for a Model Target observer
 * (recommended to use this method before starting Vuforia engine)
 */
VU_API VuResult VU_API_CALL vuModelTargetObserverSetMotionHint(VuObserver* observer, VuMotionHint motionHint);

/// \brief Get the motion hint associated to the target from a Model Target observer
VU_API VuResult VU_API_CALL vuModelTargetObserverGetMotionHint(const VuObserver* observer, VuMotionHint* motionHint);

/// \brief Get the axis-aligned bounding box associated to the target from a Model Target observer, relative to the target's frame of reference
VU_API VuResult VU_API_CALL vuModelTargetObserverGetAABB(const VuObserver* observer, VuAABB* bbox);

/// \brief Get a list of the guide views defined for a Model Target observer
/**
 * Returns all guide views associated with the Model Target observer
 *
 * \note The user has to make sure that observer and list are valid during the duration of the call,
 * otherwise the behavior is undefined.
 *
 * \note Any previous content of the given list will be removed if the operation is successful.
 *  On failure the list will not be modified.
 *
 * \note The content of the list is bound to the lifetime of the observer. Accessing the list elements after the
 * observer has been destroyed results in undefined behavior.
 *
 * \param observer The Model Target observer
 * \param list The list to fill with the guide views.
 */
VU_API VuResult VU_API_CALL vuModelTargetObserverGetGuideViews(const VuObserver* observer, VuGuideViewList* list);

/// \brief Get the name of the currently active guide view
/**
 * \note Advanced Model Targets will never report an active Guide View and the 
 * result will always be NULL.
 * 
 * \note (Legacy) Advanced Model Targets without Advanced Views will not have 
 * an active Guide View set until one is recognized and report NULL before that.
 */
VU_API VuResult VU_API_CALL vuModelTargetObserverGetActiveGuideViewName(const VuObserver* observer, const char** name);

/// \brief Set the guide view you want to be active by name
/**
 * \note If you are using an Advanced Model Target, the active Guide View is
 * set automatically when a Model Target/Guide View is recognized. Explicitly
 * setting an active Guide View is not possible in this case and will return 
 * VU_FAILED.
 */
VU_API VuResult VU_API_CALL vuModelTargetObserverSetActiveGuideViewName(VuObserver* observer, const char* name);

/// \brief Get the intrinsic parameters of the camera associated with a Guide View
VU_API VuResult VU_API_CALL vuGuideViewGetIntrinsics(const VuGuideView* guideView, VuCameraIntrinsics* cameraIntrinsics);

/// \brief Get the Guide View pose with respect to the Model Target
/**
 * Returns the pose of the Guide View camera with respect to the Model Target's coordinate system.
 * The Guide View pose determines the position and orientation of the device where tracking can be initiated.
 */
VU_API VuResult VU_API_CALL vuGuideViewGetPose(const VuGuideView* guideView, VuMatrix44F* pose);

/// \brief Set the Guide View pose with respect to the Model Target
/**
 * Sets the pose of the Guide View camera with respect to the Model Target's coordinate system.
 * The Guide View pose determines the position and orientation of the device where tracking can be initiated.
 *
 * \note Setting Guide View poses on Advanced Model Targets is not possible and will result in VU_FAILED.
 *
 * \note Calling this function causes the active guide view's image to be regenerated and the previous image to be invalidated.
 * Subsequent calls to vuGuideViewGetImage of the active guide view will return the new image.
 *
 */
VU_API VuResult VU_API_CALL vuGuideViewSetPose(VuGuideView* guideView, const VuMatrix44F* pose);

/// \brief Get the Guide View image
/**
 * The image returned is a simplified representation of the Model Target object at the pose
 * returned by getPose().
 *
 * \note The image is rendered with the latest available camera profile or a default profile if no camera is available. Thus, the guide
 * view image will look different depending on whether the observer was created before or after the Engine was started.
 *
 * \note If the guide view's pose is changed, the guide view's image is regenerated and the previous image is invalidated.
 *
 * \note This function only returns the correct image, if the guide view is active. For deactivated guide views, the image will be empty.
 *
 * \note The lifetime of the returned image is bound to the lifetime of the guide view but expires if the guide view's pose is changed.
 */
VU_API VuResult VU_API_CALL vuGuideViewGetImage(const VuGuideView* guideView, VuImage** image);

/// \brief Get the name of a Guide View
/**
 * \note The lifetime of the returned string is bound to the lifetime of the guide view.
 */
VU_API VuResult VU_API_CALL vuGuideViewGetName(const VuGuideView* guideView, const char** name);

/// \brief Turn on recognition engine for an Advanced (360) Model Target database while extended-tracking a model
/**
 * This setting enables the recognition engine when extended-tracking an existing target.
 * When set to VU_FALSE, the recognition engine is stopped for Advanced (360) databases after
 * a Model Target has been found and is never turned on again automatically. The Model Target observer
 * needs to be deactivated and re-activated in order to turn on recognition again.
 * When set to VU_TRUE, recognition is turned on as soon as an existing target is only extended-tracked.
 * If the recognition engine finds a new target in the image frame, tracking will be switched to the newly
 * identified target, resulting in tracking loss of the prior Model Target.
 * The default value is VU_TRUE.
 *
 * \note This can only be set when Vuforia is not running.
 */
VU_API VuResult VU_API_CALL vuEngineSetModelTargetRecoWhileExtendedTracked(VuEngine* engine, VuBool enable);

/// \brief Get the current setting for recognizing Advanced (360) Model Target databases while extended-tracking a model
VU_API VuResult VU_API_CALL vuEngineGetModelTargetRecoWhileExtendedTracked(VuEngine* engine, VuBool* enabled);

/// \brief Get all Model Target observations
VU_API VuResult VU_API_CALL vuStateGetModelTargetObservations(const VuState* state, VuObservationList* list);

/// \brief Get status info associated with a Model Target observation
VU_API VuResult VU_API_CALL vuModelTargetObservationGetStatusInfo(const VuObservation* observation, VuModelTargetObservationStatusInfo* statusInfo);

/// \brief Get target info associated with a Model Target observation
VU_API VuResult VU_API_CALL vuModelTargetObservationGetTargetInfo(const VuObservation* observation, VuModelTargetObservationTargetInfo* targetInfo);

/// \brief Set the active model target state by name
/**
 *  \param observer The Model Target observer to set the state of
 *  \param stateName The name of the state to activate
 *
 * \note Calling this function causes the active Guide View's image to be regenerated and the previous image to be invalidated.
 * Subsequent calls to vuGuideViewGetImage of the active Guide View will return the new image.
 */
VU_API VuResult VU_API_CALL vuModelTargetObserverSetActiveStateName(VuObserver* observer, const char* stateName);

/// \brief Get the name of the model target's active state
VU_API VuResult VU_API_CALL vuModelTargetObserverGetActiveStateName(const VuObserver* observer, const char** stateName);

/// \brief Get a list of all possible states of the model target
VU_API VuResult VU_API_CALL vuModelTargetObserverGetAvailableStates(const VuObserver* observer, VuModelTargetStateList* list);

/// \brief Get the name of the model target state
/**
 * \param state The model target state to get the name from
 * \param name Output parameter for the name of the model target state
 */
VU_API VuResult VU_API_CALL vuModelTargetStateGetName(const VuModelTargetState* state, const char** name);

/** \} */

#ifdef __cplusplus
}
#endif

#endif // _VU_MODELTARGETOBSERVER_H_
