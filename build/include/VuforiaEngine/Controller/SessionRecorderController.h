/*===============================================================================
Copyright (c) 2021 PTC Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other
countries.
===============================================================================*/

#ifndef _VU_SESSIONRECORDERCONTROLLER_H_
#define _VU_SESSIONRECORDERCONTROLLER_H_

/**
 * \file SessionRecorderController.h
 * \brief Controller to access the session recording functionality in the Vuforia Engine
 */

#include <VuforiaEngine/Core/Core.h>

#ifdef __cplusplus
extern "C"
{
#endif

/** \addtogroup SessionRecorderControllerGroup Session Recorder Controller
 * \{
 * This controller facilitates recording data from data sources such as on-device camera and sensors
 * while running Vuforia.
 */

/// \brief Session recording status
VU_ENUM(VuRecordingStatus)
{
    VU_RECORDING_STATUS_INITIALIZED                      = 0x1, ///< The recording has been created and can be started
    VU_RECORDING_STATUS_RUNNING                          = 0x2, ///< The recording is in progress
    VU_RECORDING_STATUS_COMPLETED                        = 0x3, ///< The recording has been completed and cannot be started again
    VU_RECORDING_STATUS_ERROR_SOURCES_NOT_AVAILABLE      = 0x4, ///< One or more requested sources are physically unavailable on this platform
    VU_RECORDING_STATUS_ERROR_STORAGE_LOCATION_RETRIEVAL = 0x5, ///< Unable to retrieve a suitable location for storing the data on the device
    VU_RECORDING_STATUS_ERROR_SOURCE_OPERATION           = 0x6, ///< Could not operate some requested sources
    VU_RECORDING_STATUS_ERROR_INSUFFICIENT_FREE_SPACE    = 0x7, ///< There isn't sufficient free space on the device for recording the data.
                                                                ///< A recording could not be started or was aborted due to this reason.
    VU_RECORDING_STATUS_ERROR_ORIENTATION_NOT_SUPPORTED  = 0x8, ///< A recording could not be started because of unknown orientation or
                                                                ///< was aborted due to a change in orientation during recording
    VU_RECORDING_STATUS_ERROR_ANOTHER_RECORDING_RUNNING  = 0x9  ///< Failed to start a recording because another recording is already in progress.
                                                                ///< Stop the other recording before starting a new one.
};

/// \brief Recording instance
typedef struct VuRecording_ VuRecording;

/// \brief Recording list handle
typedef struct VuRecordingList_ VuRecordingList;

/// \brief Recording sources
typedef struct VuRecordingSources
{
    /// \brief Record camera images and device poses
    /**
     * \note The default value is VU_TRUE.
     * \note Pose data is not available on all devices. 
     * If pose details unavailable, only camera images are recorded.
     */
    VuBool camera;

    /// \brief Data from accelerometer, gyroscope, and magnetometer
    /**
     * \note The default value is VU_TRUE.
     * \note Not all sensors are available on all devices. 
     * In that case only data from available sensors is recorded.
     */
    VuBool sensors;
} VuRecordingSources;

/// \brief Default recording sources
/**
 * \note Use this function to initialize the VuRecordingSources data structure with default values.
 */
VU_API VuRecordingSources VU_API_CALL vuRecordingSourcesDefault();

/// \brief The frame rate at which camera images are recorded
VU_ENUM(VuRecordingFrameRate)
{
    VU_RECORDING_FRAME_RATE_AUTO = 0x1, ///< Let Vuforia automatically choose the option that will provide
                                        ///< the best performance during recording. This is the default
    VU_RECORDING_FRAME_RATE_FULL = 0x2, ///< Record camera images at the full frame rate of the camera
    VU_RECORDING_FRAME_RATE_HALF = 0x3  ///< Record camera images at half the frame rate of the camera
};

/// \brief The scale factor to be applied to camera images before recording
VU_ENUM(VuRecordingImageScale)
{
    VU_RECORDING_IMAGE_SCALE_AUTO = 0x1, ///< Let Vuforia automatically choose the option that will provide the best
                                         ///< performance during recording. This is the default
    VU_RECORDING_IMAGE_SCALE_FULL = 0x2, ///< Record camera images at full resolution
    VU_RECORDING_IMAGE_SCALE_HALF = 0x3  ///< Downsample camera images to half size along both dimensions
};

/// \brief Session recording format
/**
 * \note Currently only one format is currently supported. Others might be added in the future.
 */
VU_ENUM(VuRecordingFormat)
{
    VU_RECORDING_FORMAT_DEFAULT = 0x1        ///< Default format
};

/// \brief Configuration options for a recording session
typedef struct VuRecordingConfig 
{
    /// \brief The sources that should be recorded
    /**
     * \note For the default value see the documentation of VuRecordingSources and vuRecordingSourcesDefault().
     */
    VuRecordingSources sources;

    /// \brief The frame rate to record camera images at
    /**
     * \note The default value is VU_RECORDING_FRAME_RATE_AUTO.
     * \note If overriding the default recording frame rate, please note that this feature is for advanced users who are sure
     * that their devices are powerful enough to handle recording frame rates that are higher than the default.
     */
    VuRecordingFrameRate frameRate;

    /// \brief The scale applied to the camera images when recorded
    /**
     * \note The default value is VU_RECORDING_IMAGE_SCALE_AUTO.
     * \note If overriding the default image scale, please note that this feature is for advanced users who are sure that their devices
     * are powerful enough to handle recording camera images at a scale that is higher than the default.
     */
    VuRecordingImageScale scale;

    /// \brief recording format (only uncompressed is currently supported, see above)
    /**
     * \note The default value is VU_RECORDING_FORMAT_DEFAULT.
     */
    VuRecordingFormat format;

    /// \brief Flag to control whether a recording should start immediately on creation
    /**
     * \note The default value is VU_FALSE.
     */
    VuBool start;
} VuRecordingConfig;

/// \brief Default configuration for a recording session
/**
 * \note Use this function to initialize the VuRecordingConfig data structure with default values.
 */
VU_API VuRecordingConfig VU_API_CALL vuRecordingConfigDefault();

/// \brief Retrieve Session Recorder Controller to get access to session recording-specific functionality in Engine
VU_API VuResult VU_API_CALL vuEngineGetSessionRecorderController(const VuEngine* engine, VuController** controller);

/// \brief Get the default recording configuration which enables all sources supported on the current platform
/**
 * \param controller Session Recorder Controller
 * \param sources Output variable storing the supported recording sources
 * \returns VU_SUCCESS on successful retrieval of the config, VU_FAILED on failure
 */
VU_API VuResult VU_API_CALL vuSessionRecorderControllerGetDeviceCapabilities(const VuController* controller, VuRecordingSources* sources);

/// \brief Get the current camera recording frame rate
/**
 * \note After Vuforia (incl. the camera) has been initialized, this method will return a sensible default value
 * which has been tested to work.
 *
 * The output value will always be an explicit one (i.e. never VU_RECORDING_FRAME_RATE_AUTO) so the caller can determine
 * the specific value being used for recording.
 */
VU_API VuResult VU_API_CALL vuSessionRecorderControllerGetFrameRate(const VuController* controller, VuRecordingFrameRate* frameRate);

/// \brief Get the current recording image scale
/**
 * \note After Vuforia has been initialized, this method will return a sensible default value which has been tested to work
 * with the current camera device's resolution.
 *
 * The output value will always be an explicit one (i.e. never VU_RECORDING_IMAGE_SCALE_AUTO) so the caller can determine
 * the specific value being used for recording.
 */
VU_API VuResult VU_API_CALL vuSessionRecorderControllerGetImageScale(const VuController* controller, VuRecordingImageScale* scale);

/// \brief Create a new recording with the specified configuration
/**
 * \note If "start" is set to VU_TRUE in the recording configuration and another recording is 
 * already running, this call will fail. Any previously started recording needs to be stopped first.
 *  
 * \param controller Session Recorder Controller
 * \param config The configuration for the new recording
 * \param recording Output variable storing the newly created recording
 * \param status Optional output variable providing additional status information. Can be NULL.
 * \returns VU_SUCCESS on successfully creation of the recording, VU_FAILED on failure
 */
VU_API VuResult VU_API_CALL vuSessionRecorderControllerCreateRecording(VuController* controller, const VuRecordingConfig* config, VuRecording** recording, VuRecordingStatus* status);

/// \brief Get a list of all recordings from the controller
/**
 * \param controller Session Recorder Controller
 * \param recordingList List that will be filled with the recordings
 * \returns VU_SUCCESS on success, or VU_FAILED otherwise
 */
VU_API VuResult VU_API_CALL vuSessionRecorderControllerGetRecordings(const VuController* controller, VuRecordingList* recordingList);

/// \brief Destroy all recordings
/**
 * This call will also stop any ongoing recording.
 *
 * \param controller Session Recorder Controller
 * \param deleteData Set to VU_TRUE to delete all data generated by the recording instances before their destruction
 * \returns VU_SUCCESS on success, or VU_FAILED otherwise
 */
VU_API VuResult VU_API_CALL vuSessionRecorderControllerDestroyRecordings(VuController* controller, VuBool deleteData);

/// \brief Remove all previously recorded sequences from the device storage
/**
 * \note This call will delete ALL existing recordings from device storage regardless
 * whether it belongs to a VuRecording that was created during this Vuforia session.
 * Any ongoing recording will be stopped and all existing recording instances will be destroyed.
 *
 * \param controller Session Recorder Controller
 * \returns VU_SUCCESS if all recorded data was removed successfully, or VU_FAILED otherwise
 */
VU_API VuResult VU_API_CALL vuSessionRecorderControllerCleanRecordedData(VuController* controller);

/// \brief Start the recording
/**
 * \note If another recording is already running, this call will fail. Any previously started recording
 * needs to be stopped first.
 *
 * \param recording The current recording
 * \param status Optional output variable providing additional status information. Can be NULL.
 * \returns VU_SUCCESS on successfully start of the recording, VU_FAILED on failure
 */
VU_API VuResult VU_API_CALL vuRecordingStart(VuRecording* recording, VuRecordingStatus* status);

/// \brief Stop the current recording
/**
 * \note The recording will be stopped automatically when vuEngineStop() is called or when it is destroyed.
 *
 * \param recording The current recording
 * \returns VU_SUCCESS on successfully stopping the recording, VU_FAILED on failure
 */
VU_API VuResult VU_API_CALL vuRecordingStop(VuRecording* recording);

/// \brief Get the path where the data for this recording is stored
/**
 * \param recording The current recording
 * \param path The absolute path to where the recording is stored or NULL if the recording has not been started yet
 * \returns VU_SUCCESS on successful retrieval of the path, VU_FAILED on failure
 */
VU_API VuResult VU_API_CALL vuRecordingGetPath(const VuRecording* recording, const char** path);

/// \brief Get the status of a recording
/**
 * \param recording The current recording
 * \param status Current recording status
 * \returns VU_SUCCESS on successfully getting status, VU_FAILED on failure
 */
VU_API VuResult VU_API_CALL vuRecordingGetStatus(const VuRecording* recording, VuRecordingStatus* status);

/// \brief Destroy a recording instance
/**
 * \note The recording will be stopped if it is running.
 *
 * \param recording Recording instance
 * \param deleteData If set to VU_TRUE, the recorded data in device storage will be deleted as well
 * \returns VU_SUCCESS on success, VU_FAILED on failure
 */
VU_API VuResult VU_API_CALL vuRecordingDestroy(VuRecording* recording, VuBool deleteData);

/// \brief Create a recording list
VU_API VuResult VU_API_CALL vuRecordingListCreate(VuRecordingList** list);

/// \brief Get the number of elements in a recording list
VU_API VuResult VU_API_CALL vuRecordingListGetSize(const VuRecordingList* list, int32_t* listSize);

/// \brief Get an element in a recording list
VU_API VuResult VU_API_CALL vuRecordingListGetElement(const VuRecordingList* list, int32_t element, VuRecording** recording);

/// \brief Destroy a recording list
VU_API VuResult VU_API_CALL vuRecordingListDestroy(VuRecordingList* list);

/** \} */

#ifdef __cplusplus
}
#endif

#endif // _VU_SESSIONRECORDERCONTROLLER_H_
