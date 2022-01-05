/*===============================================================================
Copyright (c) 2021 PTC Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other
countries.
===============================================================================*/

#ifndef _VU_VUFORIAENGINE_H_
#define _VU_VUFORIAENGINE_H_

/**
 * \file VuforiaEngine.h
 * \brief Umbrella header for the Vuforia Engine
 */

// ======== FUNDAMENTAL VUFORIA API CONCEPTS ========

/** \addtogroup APIConceptsGroup API Concepts
 * \{
 *
 * \b Syntax
 *
 * All types, data structures, functions start with a \b Vu prefix using the following notation:
 * - \b vuMethodName for functions and procedures
 * - \b VuType for type (e.g. public typedef, opaque type, enum, struct, etc)
 * - \b VU_VALUE for any enum values or define values
 *
 * \b Type
 *
 * The Vuforia Engine combines the usage of opaque pointer (handle) and public struct for data structure
 * manipulation in the API. Structs are mainly used for configurations or for output parameters and
 * contain only value parameters.
 *
 * For container data types such as a list, you need to use a dedicated API for creating, destroying, accessing elements,
 * e.g. for a list containing elements of type "Object" use
 *
 * - vuObjectListCreate(ObjectList** list)
 * - vuObjectListDestroy(ObjectList* list)
 * - vuMyObjectListGetSize(const ObjectList* list, int32_t* listSize)
 * - vuObjectListGetElement(const ObjectList* list, int32_t elementIndex, Object** element)
 *
 * Please be aware of padding when using a Vuforia structure directly if you want to wrap the API in another
 * language.
 *
 * \b Methods
 *
 * All methods are using the \b VU_API preprocessor macro for export and \b VU_API_CALL for call convention.
 * Method syntax uses the following convention:
 *
 * \b return_value \b vuObjectTypeAction(ObjectType* \b , \b [param]) when you want to execute the action on an object of type ObjectType.
 *
 * Common actions are Create, Destroy, Get, Set, Acquire, Release.
 *
 * Return value:
 * - Most methods return VuResult to indicate execution success / failure
 * - Some utility methods (e.g. math) will return a value that allows chaining such calls easily (e.g. mathematical expression)
 *
 * Parameters:
 * - First parameter is usually a handle to an object (exceptions incluide utility methods)
 * - Output parameters are at the end of patameter list
 *
 * Please note that the API does not perform any sanity checks to catching passing NULL or invalid values (e.g. already
 * freed memory).
 *
 * Some of the Get() methods have an associated Has() method that should be used prior to Get() to test whether
 * there is a valid object instance to retrieve in case the returned object is optional or not yet available. If there is
 * no valid instance, then the getter method will return VU_FAILED. For examples, see e.g. vuObservationHasPoseInfo() and
 * vuObservationGetPoseInfo().
 *
 * \b Ownership
 *
 * Objects owned by the application are created with \b vuCreate..()
 * and need to be destroyed with \b vuDestroy..() methods.
 *
 * Some objects owned by Vuforia are just returned as a reference. They can be recognized as using a method
 * \b vuAcquire..(). They always need to be released with a corresponding \b vuRelease..() method.
 *
 * For output parameters returned by value no additional call is necessary.
 *
 * \b Versioning
 *
 * At compilation time you can access the \b VU_VERSION_MAJOR|MINOR|PATCH|BUILD preprocessor values.
 *
 * At runtime, you can query the Vuforia engine version with vuEngineGetLibraryVersion().
 *
 * \b Multithreading and thread safety
 *
 * The Vuforia API is NOT thread-safe and API functions should NOT be called concurrently from multiple threads.
 * It is the responsibility of the client code to ensure that concurrent calls from multiple threads to Vuforia API
 * functions are synchronized. Failing to protect these API calls from concurrent access in multiple threads might
 * result in undefined behavior including memory corruption, instability or crashes.
 *
 * \b Vuforia callbacks
 *
 * Vuforia Engine callbacks are executed on a dedicated internal thread. Callback implementations in client code need
 * to be synchronized with other threads in your client code. Note that Vuforia Engine uses several other threads
 * internally to operate the AR pipeline.
 *
 * The Vuforia Engine internally synchronizes all API calls made from a client thread with the callback thread.
 * All API calls are performed in a blocking manner and the internal state is guaranteed to be updated once control
 * returns to user code. As a consequence, changes to the internal AR pipeline (e.g. observer manipulation via 
 * functions such as vuObserverActivate(), vu*ObserverSetTargetScale(), etc.) may take one camera frame to process
 * until they return. API calls are executed immediately when Vuforia is not running or called from within the state
 * handler callback on the camera thread.
 *
 * Due to the synchronous nature of the Vuforia API internals, it is possible that the Vuforia State obtained via
 * the registered state handler or via vuEngineAcquireLatestState() may not reflect changes made during the last camera
 * frame. Observations from an observer deactivated or destroyed during the last frame might still be reported.
 *
 * \b Callbacks and reentrancy
 *
 * The Vuforia API allows re-entrant calls to be performed during callbacks such as the camera state handler or the
 * Cloud Image Target observation handler, except calls controlling the Vuforia Engine's lifecycle such as vuEngineStop()
 * or vuEngineDestroy(), as well as functions modifying the callback handler like vuEngineRegisterStateHandler().
 * Functions that cannot be invoked during a callback are highlighted explicitly in the respective API documentation.
 * Client code needs to ensure that re-entrant calls from the callback are synchronized with calls from other application
 * threads as mentioned earlier in this section.
 *
 * \b Documentation
 *
 * Inline documentation is using Doxygen formatting with the C++ convention.
 */

/** \} */

// ======== VUFORIA ENGINE API HEADER FILES ========

// Core data types and defines
#include <VuforiaEngine/Core/Core.h>

// Database content information
#include <VuforiaEngine/Core/Database.h>

// Linear algebra functions
#include <VuforiaEngine/Core/MathUtils.h>

// Core Engine lifecycle management and configuration
#include <VuforiaEngine/Engine/Engine.h>

// Engine configuration
#include <VuforiaEngine/Engine/DriverConfig.h>
#include <VuforiaEngine/Engine/FusionProviderConfig.h>
#include <VuforiaEngine/Engine/LicenseConfig.h>
#include <VuforiaEngine/Engine/RenderConfig.h>

#ifdef VU_PLATFORM_ANDROID
#   include <VuforiaEngine/Engine/Android/PlatformConfig_Android.h>
#endif

// Observers
#include <VuforiaEngine/Observer/AnchorObserver.h>
#include <VuforiaEngine/Observer/AreaTargetObserver.h>
#include <VuforiaEngine/Observer/CloudImageTargetObserver.h>
#include <VuforiaEngine/Observer/CylinderTargetObserver.h>
#include <VuforiaEngine/Observer/DevicePoseObserver.h>
#include <VuforiaEngine/Observer/IlluminationObserver.h>
#include <VuforiaEngine/Observer/ImageTargetObserver.h>
#include <VuforiaEngine/Observer/MeshObserver.h>
#include <VuforiaEngine/Observer/ModelTargetObserver.h>
#include <VuforiaEngine/Observer/MultiTargetObserver.h>
#include <VuforiaEngine/Observer/ObjectTargetObserver.h>
#include <VuforiaEngine/Observer/VirtualButtonObserver.h>
#include <VuforiaEngine/Observer/VuMarkObserver.h>

// Controllers
#include <VuforiaEngine/Controller/AreaTargetCaptureController.h>
#include <VuforiaEngine/Controller/CameraController.h>
#include <VuforiaEngine/Controller/PlatformController.h>
#include <VuforiaEngine/Controller/RenderController.h>
#include <VuforiaEngine/Controller/SessionRecorderController.h>

// Platform-specific controllers
#ifdef VU_PLATFORM_ANDROID
#   include <VuforiaEngine/Controller/Android/PlatformController_Android.h>
#elif defined(VU_PLATFORM_IOS)
#   include <VuforiaEngine/Controller/iOS/PlatformController_iOS.h>
#elif defined(VU_PLATFORM_UWP)
#   include <VuforiaEngine/Controller/UWP/PlatformController_UWP.h>
#endif

#endif // _VU_VUFORIAENGINE_H_
