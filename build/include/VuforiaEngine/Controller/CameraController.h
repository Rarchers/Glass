/*===============================================================================
Copyright (c) 2021 PTC Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other
countries.
===============================================================================*/

#ifndef _VU_CAMERACONTROLLER_H_
#define _VU_CAMERACONTROLLER_H_

/**
 * \file CameraController.h
 * \brief Controller to access camera-specific functionality in the Vuforia Engine
 */

#include <VuforiaEngine/Core/Core.h>

#ifdef __cplusplus
extern "C"
{
#endif

/** \addtogroup CameraControllerGroup Camera Controller
 * \{
 * This controller allows the control of camera features such as configuring the video mode,
 * focus mode, flash mode or accessing advanced camera properties.
 */

/// \brief Camera video mode description
typedef struct VuCameraVideoMode
{
    /// \brief Camera video mode preset mode
    VuCameraVideoModePreset presetMode;

    /// \brief Video frame resolution
    VuVector2I resolution;

    /// \brief Video frame rate
    float frameRate;

    /// \brief Video frame format
    VuImagePixelFormat format;
} VuCameraVideoMode;

/// \brief List of video mode available for a camera
typedef struct VuCameraVideoModeList_ VuCameraVideoModeList;

/// \brief Type for the data stored in a CameraField
VU_ENUM(VuCameraFieldDataType)
{
    VU_CAMERA_FIELD_DATA_TYPE_STRING      = 0x1, ///< Null-terminated array of characters (ASCII)
    VU_CAMERA_FIELD_DATA_TYPE_INT64       = 0x2, ///< 64-bit signed integer
    VU_CAMERA_FIELD_DATA_TYPE_FLOAT       = 0x3, ///< Single precision floating point
    VU_CAMERA_FIELD_DATA_TYPE_BOOL        = 0x4, ///< Boolean
    VU_CAMERA_FIELD_DATA_TYPE_INT64_RANGE = 0x5  ///< Array of two 64-bit signed integer values.
};

/// \brief Camera field description
typedef struct VuCameraField
{
    /// \brief The data type of the camera field
    VuCameraFieldDataType type;

    /// \brief The key to identify the camera field
    const char key[255];
} VuCameraField;

/// \brief List of properties available for a camera
typedef struct VuCameraFieldList_ VuCameraFieldList;

/// \brief Retrieve Camera Controller to get access to camera-specific functionality in Engine
VU_API VuResult VU_API_CALL vuEngineGetCameraController(const VuEngine* engine, VuController** controller);

/// \brief Get all the supported video modes for the camera
/**
 * \note If this is called before the engine is started, the camera will be accessed 
 * which may be a longer-running operation on some platforms
 */
VU_API VuResult VU_API_CALL vuCameraControllerGetVideoModes(const VuController* controller, VuCameraVideoModeList* cameraVideoModeList);

/// \brief Create a camera video mode list
VU_API VuResult VU_API_CALL vuCameraVideoModeListCreate(VuCameraVideoModeList** list);

/// \brief Get number of elements in a camera video mode list
VU_API VuResult VU_API_CALL vuCameraVideoModeListGetSize(const VuCameraVideoModeList* list, int32_t* listSize);

/// \brief Get an element in a camera video mode list
VU_API VuResult VU_API_CALL vuCameraVideoModeListGetElement(const VuCameraVideoModeList* list, int32_t element, VuCameraVideoMode* videoMode);

/// \brief Destroy a camera video mode list
VU_API VuResult VU_API_CALL vuCameraVideoModeListDestroy(VuCameraVideoModeList* list);

/// \brief Get the the currently active video mode of the camera
VU_API VuResult VU_API_CALL vuCameraControllerGetActiveVideoMode(const VuController* controller, VuCameraVideoModePreset* cameraVideoModePreset);

/// \brief Set the current video mode of the camera from the list of supported presets
/**
 * \note This function can only be called before the engine is started. To change the video mode after the engine is started,
 * stop the engine, then change the video mode and restart it again.
 */
VU_API VuResult VU_API_CALL vuCameraControllerSetActiveVideoMode(VuController* controller, VuCameraVideoModePreset cameraVideoModePreset);

/// \brief Get the current flash mode of the camera
VU_API VuResult VU_API_CALL vuCameraControllerGetFlashMode(const VuController* controller, VuBool* flashMode);

/// \brief Set the flash mode of the camera
/// \note This function can only be called after the engine has been started
VU_API VuResult VU_API_CALL vuCameraControllerSetFlashMode(VuController* controller, VuBool flashMode);

/// \brief Get the current focus mode of the camera
VU_API VuResult VU_API_CALL vuCameraControllerGetFocusMode(const VuController* controller, VuCameraFocusMode* focusMode);

/// \brief Set the focus mode of the camera
/// \note This function can only be called after the engine has been started
VU_API VuResult VU_API_CALL vuCameraControllerSetFocusMode(VuController* controller, VuCameraFocusMode focusMode);

/// \brief Get all the supported video modes for the camera
VU_API VuResult VU_API_CALL vuCameraControllerGetSupportedCameraFields(const VuController* controller, VuCameraFieldList* cameraFieldList);

/// \brief Create a camera field list
VU_API VuResult VU_API_CALL vuCameraFieldListCreate(VuCameraFieldList** list);

/// \brief Get number of elements in a camera field list
VU_API VuResult VU_API_CALL vuCameraFieldListGetSize(const VuCameraFieldList* list, int32_t* listSize);

/// \brief Get an element in a camera field list
VU_API VuResult VU_API_CALL vuCameraFieldListGetElement(const VuCameraFieldList* list, int32_t element, VuCameraField* cameraField);

/// \brief Destroy a camera field list
VU_API VuResult VU_API_CALL vuCameraFieldListDestroy(VuCameraFieldList* list);

/// \brief Read a camera field of type string
/**
 * \param controller Camera Controller
 * \param key Key for the value to read. Must match one of the VuCameraField.key keys as returned in a camera field list
 * \param value Output variable set to the string value of the requested key
 * \param maxLength Maximum length of returned value
 * \returns VU_SUCCESS on success, VU_FALSE on failure or if the requested key could not be found
 */
VU_API VuResult VU_API_CALL vuCameraControllerGetFieldString(const VuController* controller, const char* key, char* value, int32_t maxLength);

/// \brief Write a camera field value of type string
/**
 * \param controller Camera Controller
 * \param key Key for the value to write. Must match one of VuCameraField.key keys as returned in a camera field list
 * \param value The string value to write
 * \returns VU_SUCCESS on success, VU_FALSE on failure or if the requested key could not be found
 */
VU_API VuResult VU_API_CALL vuCameraControllerSetFieldString(VuController* controller, const char* key, const char* value);

/// \brief Read a camera field of type int64
/**
 * \param controller Camera Controller
 * \param key Key for the value to read. Must match one of VuCameraField.key keys as returned in a camera field list
 * \param value Output variable set to the 64-bit integer value of the requested key
 * \returns VU_SUCCESS on success, VU_FALSE on failure or if the requested key could not be found
 */
VU_API VuResult VU_API_CALL vuCameraControllerGetFieldInt64(const VuController* controller, const char* key, int64_t* value);

/// \brief Write a camera field value of type int64
/**
 * \param controller Camera Controller
 * \param key Key for The value to write. Must match one of VuCameraField.key keys as returned in a camera field list
 * \param value The 64-bit integer value to write
 * \returns VU_SUCCESS on success, VU_FALSE on failure or if the requested key could not be found
 */
VU_API VuResult VU_API_CALL vuCameraControllerSetFieldInt64(VuController* controller, const char* key, int64_t value);

/// \brief Read a camera field of type float
/**
 * \param controller Camera Controller
 * \param key Key for the value to read. Must match one of VuCameraField.key keys as returned in a camera field list
 * \param value Output variable set to the float value of the requested key
 * \returns VU_SUCCESS on success, VU_FALSE on failure or if the requested key could not be found
 */
VU_API VuResult VU_API_CALL vuCameraControllerGetFieldFloat(const VuController* controller, const char* key, float* value);

/// \brief Write a camera field value of type float
/**
 * \param controller Camera Controller
 * \param key Key for the value to write. Must match one of VuCameraField.key keys as returned in a camera field list
 * \param value The float value to write
 * \returns VU_SUCCESS on success, VU_FALSE on failure or if the requested key could not be found
 */
VU_API VuResult VU_API_CALL vuCameraControllerSetFieldFloat(VuController* controller, const char* key, float value);

/// \brief Read a camera field of type Boolean
/**
 * \param controller Camera Controller
 * \param key Key for the value to read. Must match one of VuCameraField.key keys as returned in a camera field list
 * \param value Output variable set to the Boolean value of the requested key
 * \returns VU_SUCCESS on success, VU_FALSE on failure or if the requested key could not be found.
 */
VU_API VuResult VU_API_CALL vuCameraControllerGetFieldBool(const VuController* controller, const char* key, VuBool* value);

/// \brief Write a camera field value of type Boolean
/**
 * \param controller Camera Controller
 * \param key Key for the value to write. Must match one of VuCameraField.key keys as returned in a camera field list
 * \param value The Boolean value to write
 * \returns VU_SUCCESS on success, VU_FALSE on failure or if the requested key could not be found
 */
VU_API VuResult VU_API_CALL vuCameraControllerSetFieldBool(VuController* controller, const char* key, VuBool value);

/// \brief Read a camera field of type int64 range
/**
 * \param controller Camera Controller
 * \param key Key for the value to read. Must match one of VuCameraField.key keys as returned in a camera field list
 * \param value Output variable set to the int64 range value (pointer to an int64[2] array) of the requested key
 * \returns VU_SUCCESS on success, VU_FALSE on failure or if the requested key could not be found
 */
VU_API VuResult VU_API_CALL vuCameraControllerGetFieldInt64Range(const VuController* controller, const char* key, int64_t* value);

/// \brief Write a camera field value of type int64 range
/**
 * \param controller Camera Controller
 * \param key Key for the value to write. Must match one of VuCameraField.key keys as returned in a camera field list
 * \param value The int64 range value (int64[2] array) to write
 * \returns VU_SUCCESS on success, VU_FALSE on failure or if the requested key could not be found
 */
VU_API VuResult VU_API_CALL vuCameraControllerSetFieldInt64Range(VuController* controller, const char* key, int64_t value[2]);

/// \brief Get list of image formats registered to be returned with the camera frame
VU_API VuResult VU_API_CALL vuCameraControllerGetRegisteredImageFormats(const VuController* controller, VuImagePixelFormatList* list);

/// \brief Register an image format to be returned with the camera frame
/**
 * \note This function can only be called after the engine has been started
 *
 * \note Please note that not all formats can be registered as the supported conversions depend on the native camera format
 */
VU_API VuResult VU_API_CALL vuCameraControllerRegisterImageFormat(VuController* controller, VuImagePixelFormat format);

/// \brief Unregister an image format to be returned with the camera frame
/// \note This function can only be called after the engine has been started
VU_API VuResult VU_API_CALL vuCameraControllerUnregisterImageFormat(VuController* controller, VuImagePixelFormat format);

/** \} */

#ifdef __cplusplus
}
#endif

#endif // _VU_CAMERACONTROLLER_H_
