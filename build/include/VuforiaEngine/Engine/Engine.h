/*===============================================================================
Copyright (c) 2021 PTC Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other
countries.
===============================================================================*/

#ifndef _VU_ENGINE_H_
#define _VU_ENGINE_H_

/**
 * \file Engine.h
 * \brief Data structures, data types, constants and functions related to the configuration and lifecycle management of core Engine objects
 */

#include <VuforiaEngine/Core/Core.h>

#ifdef __cplusplus
extern "C"
{
#endif

// ======== ENGINE LIFECYCLE ========

/** \addtogroup EngineLifecycleGroup Engine
 * \{
 */

/// \brief Vuforia Engine instance creation error codes
/**
 * \note Additional errors are related to engine configuration,
 * see the respective error code for each engine configuration type
 */
VU_ENUM(VuEngineCreationError)
{
    VU_ENGINE_CREATION_ERROR_NONE                 = 0x0, ///< No error
    VU_ENGINE_CREATION_ERROR_DEVICE_NOT_SUPPORTED = 0x1, ///< The device is not supported
    VU_ENGINE_CREATION_ERROR_PERMISSION_ERROR     = 0x2, ///< One or more permissions required by Vuforia Engine are missing or not granted by user
                                                         ///< (e.g. the user may have denied camera access to the app)
    VU_ENGINE_CREATION_ERROR_LICENSE_ERROR        = 0x3, ///< A valid license configuration is required
    VU_ENGINE_CREATION_ERROR_INITIALIZATION       = 0x4  ///< An error occurred during initialization of the Vuforia Engine instance (e.g. an instance already exists)
};

/** \} */

// ======== VUFORIA ENGINE CONFIGURATION ========

/** \addtogroup EngineConfigGroup Engine Configuration
 * \{
 */

/// \brief Vuforia EngineConfig handle
typedef struct VuEngineConfigSet_ VuEngineConfigSet;

/// \brief Create a container to hold a list of EngineConfig configuration data
VU_API VuResult VU_API_CALL vuEngineConfigSetCreate(VuEngineConfigSet** configSet);

/// \brief Destroy a container holding a list of EngineConfig configuration data
VU_API VuResult VU_API_CALL vuEngineConfigSetDestroy(VuEngineConfigSet* configSet);

/// \brief Return the number of elements in the list of EngineConfig configuration data in a container
VU_API VuResult VU_API_CALL vuEngineConfigSetGetSize(const VuEngineConfigSet* configSet, int32_t* setSize);

/** \} */

// ======== VUFORIA ENGINE LIFECYCLE MANAGEMENT ========

/** \addtogroup EngineLifecycleGroup Engine
 * \{
 * Engine is the main entry point for interacting with the Vuforia SDK. It is an instance of the Vuforia Engine.
 *
 * A Vuforia Engine instance can be created, destroyed and should be coupled with the lifecycle of your application.
 *
 * A Vuforia Engine instance maintains a list of Observers that are used to observe the real world and report
 * Observations (see Observers section).
 *
 * A Vuforia Engine instance can be configured with a set of EngineConfig data structures to define the general
 * behavior of the Engine (see EngineConfig section).
 */

// ENGINE INSTANCE LIFECYCLE MANAGEMENT

/// \brief Vuforia Engine handle
typedef struct VuEngine_ VuEngine;

/// \brief Create a Vuforia Engine instance
/**
 * \param engine Engine instance to be created
 * \param configSet Configuration set used for creating the Engine instance
 * \param errorCode Optional error code variable (may be set to nullptr)
 * \returns VU_SUCCEED if the Engine instance creation was successful, otherwise VU_FAILED to indicate an error,
 * which you can check by inspecting the value of \p errorCode.
 *
 * \note The parameter \p errorCode has the generic type VuErrorCode and not the more specific
 * VuEngineCreationError because engine creation may also fail due to configuration reasons.
 * Engine creation error codes have 2 main categories:
 *    - VuEngineCreationError: engine creation errors not related to configuration-specific errors
 *    - Vu*ConfigError: engine creation errors related to a specific configuration, e.g. VuLicenseConfigError
 *      for license configuration-specific errors
 * All of these specific error types are converted to VuErrorCode when an error value is reported, so we can
 * cover them all here.
 */
VU_API VuResult VU_API_CALL vuEngineCreate(VuEngine** engine, const VuEngineConfigSet* configSet, VuErrorCode* errorCode);

/// \brief Destroy a Vuforia Engine instance
/**
 * \note This method will fail if called reentrant from an API callback
 */
VU_API VuResult VU_API_CALL vuEngineDestroy(VuEngine* engine);

/// \brief Start a Vuforia Engine instance
VU_API VuResult VU_API_CALL vuEngineStart(VuEngine* engine);

/// \brief Destroy a Vuforia Engine instance
/**
 * \note This method will fail if called reentrant from an API callback
 */
VU_API VuResult VU_API_CALL vuEngineStop(VuEngine* engine);

/// \brief Return VU_TRUE if the given Vuforia Engine instance has been started
VU_API VuBool VU_API_CALL vuEngineIsRunning(const VuEngine* engine);

// ENGINE VERSION

/// \brief Vuforia library version information
typedef struct VuLibraryVersionInfo
{
    /// \brief Full library version as: major.minor.patch+build
    const char* fullLibraryVersion;
    
    /// \brief Major version
    int32_t majorVersion;
    
    /// \brief Minor version
    int32_t minorVersion;
    
    /// \brief Patch version
    int32_t patchVersion;
    
    /// \brief Build version
    int32_t buildVersion;
} VuLibraryVersionInfo;

/// \brief Get Vuforia Engine version information
/**
 * \returns A data structure with the Vuforia Engine version
 */
VU_API VuLibraryVersionInfo VU_API_CALL vuEngineGetLibraryVersion();

/** \} */

// ======== VUFORIA OBSERVER AND OBSERVATION MANAGEMENT ========

/** \addtogroup ObserverObservationManagementGroup Observer and Observation Management
 * \{
 * Observers are created by observer-specific creation functions consuming an observer-specific
 * configuration data stucture Vu<ObserverType>Config, typically with the following signature:
 *
 * VuResult vuEngineCreate<ObserverType>Observer(VuEngine* engine, VuObserver** observer,
 *    const Vu<ObserverType>Config* config, Vu<ObserverType>CreationError* errorCode);
 *
 * The configuration data structure can be initialized with default parameters with a dedicated 
 * function vu<ObserverType>ConfigDefault(), and can be customized with desired settings.
 * When creation fails, the creation function outputs an observer-specific error code of type
 * Vu<ObserverType>CreationError.
 *
 * The observer-specific configurations have a field of type VuBool called \p activate, which
 * determines whether observer-internal resources should be activated automatically upon
 * creation, or they require explicit activation by calling vuObserverActivate(). All observers
 * are automatically deactivated when stopping the Vuforia Engine via vuEngineStop(), or they
 * can be deactivated individually by calling vuObserverDeactivate().
 *
 * If an observer is configured to be automatically activated on creation and the activation
 * fails, then the \p errorCode parameter supplied to the observer creation method (e.g.
 * vuEngineCreate<ObserverType>Observer()) is set to VU_<OBSERVER_TYPE>_CREATION_ERROR_AUTOACTIVATION_FAILED
 * for all observer types where applicable.
 *
 * Individual observers can be destroyed with the general vuObserverDestroy() function. A list
 * of observers can be destroyed by calling vuObserversDestroy() with a VuObserverList.
 * Neither of the destruction functions are observer-specific, unlike the observer creation methods.
 * 
 * Note that for modification of observers (vu<ObserverType>ObserverSet<Property>()) or for 
 * creation of observers with non-default optional arguments (for example "scale") it is 
 * recommended that all observers from the same database are deactivated as these operations 
 * can be expensive otherwise - especially when the database contains a large amount of 
 * targets.
 */


/// \brief Motion hint type
VU_ENUM(VuMotionHint)
{
    VU_MOTION_HINT_STATIC   = 0x1, ///< Optimize performance for objects that remain at the
                                   ///< same location throughout the experience. Setting this
                                   ///< value allows Vuforia to reduce CPU and power
                                   ///< consumption when a device pose observer is used.
    VU_MOTION_HINT_ADAPTIVE = 0x2, ///< Automatically optimize performance for experiences
                                   ///< ranging from mostly static objects to moving objects.
                                   ///< \note Some target types do not support the full motion
                                   ///< range, e.g. Model Target only allows limited object
                                   ///< motion. Refer to the specific target documentation for
                                   ///< more information on their behavior.
    VU_MOTION_HINT_DYNAMIC  = 0x3  ///< Optimize performance for objects that are moved constantly,
                                   ///< e.g. for interacting with the environment.
};

// OBSERVER MANAGEMENT

/// \brief Vuforia Observer handle
typedef struct VuObserver_ VuObserver;

/// \brief Observer type
typedef int32_t VuObserverType;

/// \brief Get a unique ID associated with an observer
/**
 * The ID is a positive number and is unique within a Vuforia session. It is generated at
 * runtime and is not persistent across Vuforia sessions
 */
VU_API int32_t VU_API_CALL vuObserverGetId(const VuObserver* observer);

/// \brief Get the type of an observer
VU_API VuResult VU_API_CALL vuObserverGetType(const VuObserver* observer, VuObserverType* observerType);

/// \brief Check the type of an observer
VU_API VuBool VU_API_CALL vuObserverIsType(const VuObserver* observer, VuObserverType observerType);

// OBSERVER LIST MANAGEMENT

/// \brief Vuforia ObserverList handle
typedef struct VuObserverList_ VuObserverList;

/// \brief Create an observer list
VU_API VuResult VU_API_CALL vuObserverListCreate(VuObserverList** list);

/// \brief Get number of elements in an observer list
VU_API VuResult VU_API_CALL vuObserverListGetSize(const VuObserverList* list, int32_t* listSize);

/// \brief Get an element in an observer list
VU_API VuResult VU_API_CALL vuObserverListGetElement(const VuObserverList* list, int32_t element, VuObserver** observer);

/// \brief Destroy an observer list
VU_API VuResult VU_API_CALL vuObserverListDestroy(VuObserverList* list);

// OBSERVER RETRIEVAL

/// \brief Get an observer from Vuforia Engine using its unique ID
VU_API VuResult VU_API_CALL vuEngineGetObserver(const VuEngine* engine, int32_t observerId, VuObserver** observer);

/// \brief Get all observers from Vuforia Engine
VU_API VuResult VU_API_CALL vuEngineGetObservers(const VuEngine* engine, VuObserverList* observerList);

// OBSERVER LIFECYCLE MANAGEMENT

/// \brief Destroy an observer
VU_API VuResult VU_API_CALL vuObserverDestroy(VuObserver* observer);

/// \brief Destroy multiple observers
VU_API VuResult VU_API_CALL vuObserversDestroy(VuObserverList* observerList);

/// \brief Destroy all observers in Vuforia Engine
VU_API VuResult VU_API_CALL vuEngineDestroyObservers(VuEngine* engine);

/// \brief Activate an observer
VU_API VuResult VU_API_CALL vuObserverActivate(VuObserver* observer);

/// \brief Deactivate an observer
VU_API VuResult VU_API_CALL vuObserverDeactivate(VuObserver* observer);

/// \brief Check whether an observer is activated
/**
 * \returns VU_TRUE if the observer is activated, VU_FALSE if the observer is not activated
 */
VU_API VuBool VU_API_CALL vuObserverIsActivated(const VuObserver* observer);

// OBSERVATION MANAGEMENT

/// \brief Vuforia Observation handle
typedef struct VuObservation_ VuObservation;

/// \brief Observation type
typedef int32_t VuObservationType;

/// \brief Get the type of an observation
VU_API VuResult VU_API_CALL vuObservationGetType(const VuObservation* observation, VuObservationType* observationType);

/// \brief Check the type of an observation
VU_API VuBool VU_API_CALL vuObservationIsType(const VuObservation* observation, VuObservationType observationType);

/// \brief Get ID of the observer which has created given observation
VU_API int32_t VU_API_CALL vuObservationGetObserverId(const VuObservation* observation);

// OBSERVATION DATA MANAGEMENT

/// \brief Observation pose status
VU_ENUM(VuObservationPoseStatus)
{
    VU_OBSERVATION_POSE_STATUS_NO_POSE          = 0x1, ///< No valid pose available. For details refer to the status info. The
                                                       ///< NOT_OBSERVED status info is returned if an observed object is currently
                                                       ///< not being tracked. Some observers may provide additional information for
                                                       ///< NO_POSE in their status info.
    VU_OBSERVATION_POSE_STATUS_LIMITED          = 0x2, ///< Observed object is being tracked in a limited form, and so the pose may
                                                       ///< be unreliable or degraded. For details refer to the status info
    VU_OBSERVATION_POSE_STATUS_TRACKED          = 0x3, ///< Observed object is being tracked with a valid pose
    VU_OBSERVATION_POSE_STATUS_EXTENDED_TRACKED = 0x4  ///< Observed object is being tracked using extended tracking
};

/// \brief Pose info for observation with Pose
typedef struct VuPoseInfo
{
    /// \brief Pose status
    VuObservationPoseStatus poseStatus;
    
    /// \brief Pose matrix
    VuMatrix44F pose;
} VuPoseInfo;

/// \brief Check whether an observation contains pose information
VU_API VuBool VU_API_CALL vuObservationHasPoseInfo(const VuObservation* observation);

/// \brief Get pose info associated to an observation (for observation having pose, see availability for each observer)
/**
 * \note This call fails if the observation does not contain pose information. You can verify this by calling
 * vuObservationHasPoseInfo() and checking its return value: VU_TRUE for an observation with pose information,
 * VU_FALSE without.
 */
VU_API VuResult VU_API_CALL vuObservationGetPoseInfo(const VuObservation* observation, VuPoseInfo* poseInfo);

// OBSERVATION LIST MANAGEMENT

/// \brief Vuforia ObservationList handle
typedef struct VuObservationList_ VuObservationList;

/// \brief Create an observation list
VU_API VuResult VU_API_CALL vuObservationListCreate(VuObservationList** list);

/// \brief Get number of elements in an observation list
VU_API VuResult VU_API_CALL vuObservationListGetSize(const VuObservationList* list, int32_t* listSize);

/// \brief Get an element in an observation list
VU_API VuResult VU_API_CALL vuObservationListGetElement(const VuObservationList* list, int32_t element, VuObservation** observation);

/// \brief Destroy an observation list
VU_API VuResult VU_API_CALL vuObservationListDestroy(VuObservationList* list);

/** \} */

// ======== VUFORIA ENGINE STATE MANAGEMENT ========

/** \addtogroup EngineStateUpdateGroup State Management
 * \{
 * The Vuforia State represents a snapshot of what Vuforia Engine "knows" about the user's environment.
 * It contains all information Vuforia clients need to render an AR scene, including observations, camera
 * and rendering information. Individual state information elements can be retrieved from VuState with
 * the respective vuStateGet* getter functions, e.g. vuStateGetObservations() acquires the current list of 
 * observations generated by observers. VuState objects are immutable and their data cannot be changed after
 * creation.
 * 
 * There are 2 ways to obtain the latest Vuforia State from the Vuforia Engine:
 * 
 *   - Pull mechanism: The function vuEngineAcquireLatestState() allows the acquisition of the latest state
 *     on demand while Engine is running. State objects acquired with this mechanism must be released by
 *     calling vuStateRelease() when no longer needed to avoid leaking memory.
 * 
 *   - Push mechanism: The vuEngineRegisterStateHandler() allows the registration of a callback function of type
 *     VuStateHandler, which informs the registered client about the latest state as it becomes available. Only
 *     one callback function can be registered at one time. Callbacks keep coming from Engine as long as it is
 *     running or the client unregisters the callback by calling vuEngineRegisterStateHandler() with a nullptr
 *     as the handler function. State objects obtained with this mechanism do not need to (and cannot) be released,
 *     and are valid only during the callback scope.
 *
 * The VuState object acquired via the pull mechanism remains valid as long as the Engine instance is valid or
 * until vuStateRelease() is called to release the internal state data. The VuState object received via the push
 * mechanism remains valid during the scope of the callback. 
 *
 * If you wish to extend the lifetime beyond its original scope (e.g. to preserve state data outside the state
 * handler callback), you need to create a state reference. You can create any number of references to a VuState
 * instance by calling vuStateAcquireReference(). A state reference contains the same immutable information as
 * the VuState it has been created from but its lifetime becomes independent from it. This means that even if you
 * call vuStateRelease() on a given VuState, a reference created from it beforehand remains valid until
 * vuStateRelease() is called on the state reference.
 */

// STATE TYPE

/// \brief Vuforia State handle
typedef struct VuState_ VuState;

// STATE MANAGEMENT

/// \brief Return new state from Vuforia Engine (make sure to call release if you use this method)
/**
 * \note This call will fail if Vuforia is not running.
 * \note The state does not contain any camera frame or render state data if it is acquired
 * after calling vuEngineStart() but before the first camera frame is retrieved by Vuforia
 */
VU_API VuResult VU_API_CALL vuEngineAcquireLatestState(const VuEngine* engine, VuState** state);

/// \brief Release the given state
VU_API VuResult VU_API_CALL vuStateRelease(VuState* state);

/// \brief Acquire a new reference to the given state
VU_API VuResult VU_API_CALL vuStateAcquireReference(const VuState* state, VuState** stateOut);

// STATE UPDATE CALLBACK

/// \brief Vuforia State handler function type
typedef void (VU_API_CALL VuStateHandler)(const VuState* state, void* clientData);

/// \brief Register a state handler to get Vuforia State updates
/**
 * The registered handler function will report the Vuforia State including observations, camera and
 * rendering information at the camera frame rate.
 *
 * \note This method will fail if called reentrant from an API callback. Only one handler can be registered
 * at a given time. A handler can be unregistered by providing NULL to the method.
 *
 * \param engine Engine instance
 * \param handler The handler for receiving state updates
 * \param clientData The client data to pass with the state updates
 */
VU_API VuResult VU_API_CALL vuEngineRegisterStateHandler(VuEngine* engine, VuStateHandler* handler, void* clientData);

// STATE DATA RETRIEVAL

/// \brief Get a list of observations from the state
VU_API VuResult VU_API_CALL vuStateGetObservations(const VuState* state, VuObservationList* list);

/// \brief Get all observations with pose info from State
/**
 * \note This call returns observations that can be fed into vuObservationGetPoseInfo to retrieve the pose.
 */
VU_API VuResult VU_API_CALL vuStateGetObservationsWithPoseInfo(const VuState* state, VuObservationList* list);

/// \brief Get all observations from the state associated to a given observer.
/**
 * Returns all observations from the state that have been created by a given observer.
 *
 * \note The user has to make sure that state, observer and list are valid during the time of the call,
 * otherwise the behavior is undefined.
 *
 * \note Any previous content of the given list will be removed if the operation was successful. On failure the list will not be modified.
 *
 * \param state The state containing the observations.
 * \param observer The observer to use as a filter for the observations.
 * \param list The list to fill with the observations found.
 * \returns VU_SUCCESS on success, VU_FAILED on failure.
 */
VU_API VuResult VU_API_CALL vuStateGetObservationsByObserver(const VuState* state, const VuObserver* observer, VuObservationList* list);

/// \brief Get the camera intrinsics from the state
VU_API VuResult VU_API_CALL vuStateGetCameraIntrinsics(const VuState* state, VuCameraIntrinsics* cameraIntrinsics);

// CAMERA FRAME IN STATE

/// \brief Vuforia Camera Frame
typedef struct VuCameraFrame_ VuCameraFrame;

/// \brief Return true if the state contains camera frame data
/**
 * \note The state does not contain a camera frame if it is acquired after calling
 * vuEngineStart() but before the first camera frame is retrieved by Vuforia. There is always a
 * valid camera frame when using a VuStateHandler callback
 */
VU_API VuBool VU_API_CALL vuStateHasCameraFrame(const VuState* state);

/// \brief Get the camera frame from the state
/**
 * \note This call will fail if vuStateHasCameraFrame() returns VU_FALSE which happens if the
 * state is acquired after calling vuEngineStart() but before the first camera frame is retrieved by
 * Vuforia.
 */
VU_API VuResult VU_API_CALL vuStateGetCameraFrame(const VuState* state, VuCameraFrame** cameraFrame);

/// \brief Get index from a camera frame
VU_API VuResult VU_API_CALL vuCameraFrameGetIndex(const VuCameraFrame* cameraFrame, int64_t* index);

/// \brief Get timestamp from a camera frame
VU_API VuResult VU_API_CALL vuCameraFrameGetTimestamp(const VuCameraFrame* cameraFrame, int64_t* timestamp);

/// \brief Get camera image list from a camera frame
VU_API VuResult VU_API_CALL vuCameraFrameGetImages(const VuCameraFrame* cameraFrame, VuImageList* list);

// RENDER STATE

/// \brief Vuforia Render State
typedef struct VuRenderState
{
    // VIDEO BACKGROUND
    /// \brief Viewport settings
    VuVector4I viewport;
    
    /// \brief Video background projection matrix
    VuMatrix44F vbProjectionMatrix;
    
    /// \brief Video background mesh
    /**
     * \note Set to NULL if the state is acquired before the first camera frame is retrieved by Vuforia or
     * if running on a see-through eyewear device
     * 
     * \note The video background mesh is bound to the lifetime of the VuState it was retrieved from.
     * Do not use this pointer or the data it points to beyond the lifetime of the respective VuState.
     */
    VuMesh* vbMesh;

    // AUGMENTATION
    /// \brief View matrix (inverse of device pose)
    /**
     * \note This is set to the identity matrix if there is no active device pose observer
     */
    VuMatrix44F viewMatrix;

    /// \brief Projection matrix (using near/far plane values set in render controller)
    VuMatrix44F projectionMatrix;
} VuRenderState;

/// \brief Get the render state frame from the state
/**
 * \note All members of the render state are 0 if the state is acquired after calling
 * vuEngineStart() but before the first camera frame is retrieved by Vuforia
 * 
 * \note Viewport and video background rendering data may be 0 on some platforms when
 * Vuforia cannot auto-detect a default viewport size at engine creation time and
 * vuRenderControllerSetRenderViewConfig() was not called by the user
 */
VU_API VuResult VU_API_CALL vuStateGetRenderState(const VuState* state, VuRenderState* renderState);

/** \} */

#ifdef __cplusplus
}
#endif

#endif // _VU_ENGINE_H_
