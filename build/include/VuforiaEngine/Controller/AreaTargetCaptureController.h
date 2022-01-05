/*===============================================================================
Copyright (c) 2021 PTC Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other
countries.
===============================================================================*/

#ifndef _VU_AREATARGETCAPTURECONTROLLER_H_
#define _VU_AREATARGETCAPTURECONTROLLER_H_

/**
 * \file AreaTargetCaptureController.h
 * \brief Controller to access the Area Target Capture functionality in the Vuforia Engine
 */

#include <VuforiaEngine/Engine/Engine.h>

#ifdef __cplusplus
extern "C"
{
#endif

/** \addtogroup AreaTargetCaptureGroup Area Target Capture Controller
 * \{
 * This controller provides functionality to generate Area Targets by capturing all required data with Vuforia Engine. 
 * \note This feature is supported only on iOS devices with a LiDAR sensor.
 */

/// \brief Area Target Capture creation error
VU_ENUM(VuAreaTargetCaptureCreationError)
{
    VU_AREA_TARGET_CAPTURE_CREATION_ERROR_NONE                         = 0x0, ///< No error
    VU_AREA_TARGET_CAPTURE_CREATION_ERROR_CAPTURE_ALREADY_EXISTS       = 0x1, ///< Multiple captures are not allowed to exist at the same time
    VU_AREA_TARGET_CAPTURE_CREATION_ERROR_MISSING_AUTHENTICATION       = 0x2, ///< Missing authentication details (user authentication or authentication secret)
    VU_AREA_TARGET_CAPTURE_CREATION_ERROR_INVALID_DEVICE_POSE_OBSERVER = 0x3, ///< DevicePose Observer is null or invalid
    VU_AREA_TARGET_CAPTURE_CREATION_ERROR_INVALID_TARGET_FOLDER        = 0x4, ///< Target folder does not exist or can not be written to
    VU_AREA_TARGET_CAPTURE_CREATION_ERROR_INVALID_TARGET_NAME          = 0x5, ///< Target name does not satisfy requirements
    VU_AREA_TARGET_CAPTURE_CREATION_ERROR_FEATURE_NOT_SUPPORTED        = 0x6, ///< Feature is not supported on the current device
    VU_AREA_TARGET_CAPTURE_CREATION_ERROR_START_FAILURE                = 0x7, ///< Immediate start of the capture failed
    VU_AREA_TARGET_CAPTURE_CREATION_ERROR_INTERNAL                     = 0x8  ///< An internal error occurred while creating the capture
};

/// \brief Area Target Capture status
VU_ENUM(VuAreaTargetCaptureStatus)
{
    VU_AREA_TARGET_CAPTURE_STATUS_INITIALIZED                   = 0x1, ///< The capture was created and can be started
    VU_AREA_TARGET_CAPTURE_STATUS_PREPARING                     = 0x2, ///< The capture is performing the initial reconstruction of the environment, but a target cannot be generated yet
    VU_AREA_TARGET_CAPTURE_STATUS_CAPTURING                     = 0x3, ///< The capture created an initial reconstruction of the environment. A target can now be generated, or continue to capture more data
    VU_AREA_TARGET_CAPTURE_STATUS_PAUSED                        = 0x4, ///< The capture was paused
    VU_AREA_TARGET_CAPTURE_STATUS_STOPPED                       = 0x5, ///< The capture was stopped
    VU_AREA_TARGET_CAPTURE_STATUS_GENERATING                    = 0x6, ///< The capture is generating a target from the captured data
    VU_AREA_TARGET_CAPTURE_STATUS_COMPLETED                     = 0x7, ///< The capture successfully generated a target
    VU_AREA_TARGET_CAPTURE_STATUS_ERROR_INSUFFICIENT_FREE_SPACE = 0x8, ///< Generation failed because there is insufficient storage space
    VU_AREA_TARGET_CAPTURE_STATUS_ERROR_INSUFFICIENT_DATA       = 0x9, ///< Generation failed because the capture has not yet created an initial reconstruction of the environment
    VU_AREA_TARGET_CAPTURE_STATUS_ERROR_NO_NETWORK_CONNECTION   = 0xA, ///< Generation failed because the device has no network connection
    VU_AREA_TARGET_CAPTURE_STATUS_ERROR_AUTHORIZATION_FAILED    = 0xB, ///< Generation failed because the credentials are wrong or outdated
    VU_AREA_TARGET_CAPTURE_STATUS_ERROR_SERVICE_NOT_AVAILABLE   = 0xC, ///< Generation failed because the server was not found, is unreachable, or overloaded
    VU_AREA_TARGET_CAPTURE_STATUS_ERROR_INTERNAL                = 0xD, ///< An internal error occurred
    VU_AREA_TARGET_CAPTURE_STATUS_ERROR_CANCELED                = 0xE  ///< Generation failed because the user canceled it
};

/// \brief Area Target Capture status info
VU_ENUM(VuAreaTargetCaptureStatusInfo)
{
    VU_AREA_TARGET_CAPTURE_STATUS_INFO_NORMAL                     = 0x1, ///< The capture is running normally
    VU_AREA_TARGET_CAPTURE_STATUS_INFO_MOVEMENT_NEEDED            = 0x2, ///< The capture is initializing or relocalizing, the user should move around or return towards a previously mapped area in order to resume normal capturing.
    VU_AREA_TARGET_CAPTURE_STATUS_INFO_EXCESSIVE_MOVEMENT         = 0x3, ///< The user is moving too quickly
    VU_AREA_TARGET_CAPTURE_STATUS_INFO_CAPTURE_SIZE_LIMIT_REACHED = 0x4, ///< The capture is removing old data to make room for new data and should be stopped soon
    VU_AREA_TARGET_CAPTURE_STATUS_INFO_CAPTURE_SHOULD_STOP        = 0x5, ///< The capture is unable to add new data, the user should stop the capture
    VU_AREA_TARGET_CAPTURE_STATUS_INFO_GENERATING_PREPARING       = 0x6, ///< The capture is processing the captured data
    VU_AREA_TARGET_CAPTURE_STATUS_INFO_GENERATING_AUTHORING_FILES = 0x7, ///< The capture is generating the authoring artifacts (3dt, glb)
    VU_AREA_TARGET_CAPTURE_STATUS_INFO_GENERATING_DEVICE_DATABASE = 0x8, ///< The capture is generating the Vuforia device database (dat, xml)
    VU_AREA_TARGET_CAPTURE_STATUS_INFO_GENERATING_PACKAGES        = 0x9  ///< The capture is generating the package(s)
};

/// \brief Area Target Capture instance
typedef struct VuAreaTargetCapture_ VuAreaTargetCapture;

/// \brief Configuration options for an Area Target Capture instance
typedef struct VuAreaTargetCaptureConfig
{
    /// \brief User name for authentication with the Vuforia server
    /**
     * \note The provided string is copied, and can be freed, after the Area Target Capture instance is created.
     */
    const char* userAuth;
    
    /// \brief Secret key for authentication with the Vuforia server
    /**
     * \note The provided string is copied, and can be freed, after the Area Target Capture instance is created.
     */
    const char* secretAuth;
    
    /// \brief Target folder path
    /**
     * The target folder path can be absolute or relative. The capture will store the files it was configured to
     * generate to this path. It must exist and be writable throughout the lifetime of the capture.
     * \note The provided string is copied, and can be freed, after the Area Target Capture instance is created.
     */
    const char* targetFolder;

    /// \brief Target name
    /**
     * The name of the generated Area Target is required to respect the following restrictions:
     * - Length: 1 - 64 characters
     * - Encoding: ASCII
     * - Allowed characters: numerals (0-9), literals (a-zA-Z), dash (-), underscore (_)
     *
     * \note The provided string is copied, and can be freed, after the Area Target Capture instance is created.
     */
    const char* targetName;

    /// \brief Generate authoring artifacts
    /**
     * \note Default value is VU_TRUE
     */
    VuBool generateAuthoringFiles;

    /// \brief Generate packages
    /**
     * \note Default value is VU_FALSE. If set to VU_TRUE, \ref generateAuthoringFiles should be set to VU_TRUE, too,
     * otherwise the Area Target generation will fail.
     */
    VuBool generatePackages;

    /// \brief Device pose observer
    /**
     * \note The Area Target Capture instance can only capture data while the device pose observer is active.
     */
    VuObserver* devicePoseObserver;

    /// \brief Set to VU_TRUE to immediately start the capture after creation
    /**
     * \note Default value is VU_FALSE
     */
    VuBool start;
} VuAreaTargetCaptureConfig;

/// \brief Default capture configuration
/**
 * \note Use this function to initialize the VuAreaTargetCaptureConfig data structure with default values.
 */
VU_API VuAreaTargetCaptureConfig VU_API_CALL vuAreaTargetCaptureConfigDefault();

/// \brief Retrieve Area Target Capture Controller to get access to Area Target Capture functionality in Engine
VU_API VuResult VU_API_CALL vuEngineGetAreaTargetCaptureController(const VuEngine* engine, VuController** controller);

/// \brief Create a new capture with the specified configuration
/**
 * Any previously created capture has to be destroyed first.
 * The created capture has status \ref VU_AREA_TARGET_CAPTURE_STATUS_INITIALIZED, except if the configuration's
 * start flag is VU_TRUE, then the capture has status \ref VU_AREA_TARGET_CAPTURE_STATUS_PREPARING
 * after successful creation.
 *
 * \param controller Area Target Capture Controller
 * \param config The configuration for the new capture
 * \param capture Output variable storing the newly created capture
 * \param error Optional output variable providing additional error information. Can be NULL.
 * \returns VU_SUCCESS on successfully creation of the capture, VU_FAILED on failure
 */
VU_API VuResult VU_API_CALL vuAreaTargetCaptureControllerCreateAreaTargetCapture(VuController* controller, const VuAreaTargetCaptureConfig* config, VuAreaTargetCapture** capture, VuAreaTargetCaptureCreationError* error);

/// \brief Start the capture
/**
 * The capture starts data acquistion and switches to status \ref VU_AREA_TARGET_CAPTURE_STATUS_PREPARING.
 * Fails if the capture status is not \ref VU_AREA_TARGET_CAPTURE_STATUS_INITIALIZED.
 */
VU_API VuResult VU_API_CALL vuAreaTargetCaptureStart(VuAreaTargetCapture* capture);

/// \brief Stop the capture
/**
 * The capture stops data acquisition, and switches to status \ref VU_AREA_TARGET_CAPTURE_STATUS_STOPPED.
 * Fails if the capture status is not \ref VU_AREA_TARGET_CAPTURE_STATUS_PREPARING,
 * \ref VU_AREA_TARGET_CAPTURE_STATUS_CAPTURING or \ref VU_AREA_TARGET_CAPTURE_STATUS_PAUSED.
 * \note Only when the capture is stopped can an Area Target be generated from it.
 */
VU_API VuResult VU_API_CALL vuAreaTargetCaptureStop(VuAreaTargetCapture* capture);

/// \brief Pause the capture
/**
 * The capture pauses data acquisition, and switches to status \ref VU_AREA_TARGET_CAPTURE_STATUS_PAUSED.
 * Fails if the capture status is not \ref VU_AREA_TARGET_CAPTURE_STATUS_PREPARING or
 * \ref VU_AREA_TARGET_CAPTURE_STATUS_CAPTURING.
 */
VU_API VuResult VU_API_CALL vuAreaTargetCapturePause(VuAreaTargetCapture* capture);

/// \brief Resume the capture
/**
 * The capture resumes data acquisition, and switches to the status it was in before pause.
 * Fails if the capture status is not \ref VU_AREA_TARGET_CAPTURE_STATUS_PAUSED.
 */
VU_API VuResult VU_API_CALL vuAreaTargetCaptureResume(VuAreaTargetCapture* capture);

/// \brief Generate the Area Target from a stopped capture
/**
 * This function generates an Area Target from the Area Target Capture instance. The capture
 * is required to have status \ref VU_AREA_TARGET_CAPTURE_STATUS_STOPPED, only then will it switch to
 * status \ref VU_AREA_TARGET_CAPTURE_STATUS_GENERATING and generate a target from the captured data.
 * If target generation succeeds, the capture switches to status \ref VU_AREA_TARGET_CAPTURE_STATUS_COMPLETED.
 * If generation fails, status will hold an error status that reflects the cause of the error.
 * \note Area Target generation may take significant time to complete, thus it is advisable to call this
 * function from a different thread than the UI thread.
 * \note A target can only be generated if the capture accumulated sufficient data to switch from the
 * \ref VU_AREA_TARGET_CAPTURE_STATUS_PREPARING status to the \ref VU_AREA_TARGET_CAPTURE_STATUS_CAPTURING
 * status.
 * \note If target generation fails with \ref VU_AREA_TARGET_CAPTURE_STATUS_ERROR_NO_NETWORK_CONNECTION,
 * \ref VU_AREA_TARGET_CAPTURE_STATUS_ERROR_SERVICE_NOT_AVAILABLE or
 * \ref VU_AREA_TARGET_CAPTURE_STATUS_ERROR_CANCELED, then one may attempt to call this function again
 * to successfully generate a target.
 */
VU_API VuResult VU_API_CALL vuAreaTargetCaptureGenerate(VuAreaTargetCapture* capture, VuAreaTargetCaptureStatus* status);

/// \brief Get status of the capture
VU_API VuResult VU_API_CALL vuAreaTargetCaptureGetStatus(const VuAreaTargetCapture* capture, VuAreaTargetCaptureStatus* status);

/// \brief Get status info for the capture
VU_API VuResult VU_API_CALL vuAreaTargetCaptureGetStatusInfo(const VuAreaTargetCapture* capture, VuAreaTargetCaptureStatusInfo* statusInfo);

/// \brief Attempt to cancel a concurrently running target generation
/**
 * A helper function to signal to a capture that it should abort a running target generation. In rare cases,
 * it is necessary to call this function multiple times to successfully cancel the running target generation.
 * If the target generation has been canceled successfully, the capture will switch to status
 * \ref VU_AREA_TARGET_CAPTURE_STATUS_ERROR_CANCELED. The latter allows for target generation to be called again.
 * \returns VU_SUCCESS if the capture is in status VU_AREA_TARGET_CAPTURE_STATUS_GENERATING, VU_FAILED otherwise.
 */
VU_API VuResult VU_API_CALL vuAreaTargetCaptureCancelGeneration(VuAreaTargetCapture* capture);

/// \brief Get progress information of the current generation in the range [0.0f, 1.0f]
/**
 * \note Fails if the capture is not in status \ref VU_AREA_TARGET_CAPTURE_STATUS_GENERATING or
 * \ref VU_AREA_TARGET_CAPTURE_STATUS_COMPLETED
 */
VU_API VuResult VU_API_CALL vuAreaTargetCaptureGetGenerationProgress(const VuAreaTargetCapture* capture, float* progress);

/// \brief Get estimated time remaining to complete the current generation in seconds.
/**
 * The capture has to be in status \ref VU_AREA_TARGET_CAPTURE_STATUS_GENERATING or \ref VU_AREA_TARGET_CAPTURE_STATUS_COMPLETED
 * for this call to succeed. It may take few seconds after the generation started for the time estimate to become available.
 * If the time estimate is not available this call will fail.
 */
VU_API VuResult VU_API_CALL vuAreaTargetCaptureGetGenerationTimeEstimate(const VuAreaTargetCapture* capture, uint32_t* remainingTimeSeconds);

/// \brief Destroy the given capture instance
/**
 * \note It is the user's responsibility to make sure that no concurrent target generation is running when destroying the capture.
 */
VU_API VuResult VU_API_CALL vuAreaTargetCaptureDestroy(VuAreaTargetCapture* capture);

/** \} */

#ifdef __cplusplus
}
#endif

#endif // _VU_AREATARGETCAPTURECONTROLLER_H_
