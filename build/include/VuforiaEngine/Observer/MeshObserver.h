/*===============================================================================
Copyright (c) 2021 PTC Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other
countries.
===============================================================================*/

#ifndef _VU_MESHOBSERVER_H_
#define _VU_MESHOBSERVER_H_

/**
 * \file MeshObserver.h
 * \brief Observer for the Mesh feature
 */

#include <VuforiaEngine/Engine/Engine.h>
#include <VuforiaEngine/Controller/AreaTargetCaptureController.h>

#ifdef __cplusplus
extern "C"
{
#endif

/** \addtogroup MeshObserverGroup Mesh Feature
 * \{
 * The Mesh Observer enables the retrieval of meshes provided by a specific source, such as an
 * Area Target Capture instance.
 */

/// \brief Configuration error for Mesh observer creation with Area Target Capture
VU_ENUM(VuMeshAreaTargetCaptureCreationError)
{
    VU_MESH_AREA_TARGET_CAPTURE_CREATION_ERROR_NONE                  = 0x00, ///< No error
    VU_MESH_AREA_TARGET_CAPTURE_CREATION_ERROR_INTERNAL              = 0x01, ///< An internal error occurred while creating the observer
    VU_MESH_AREA_TARGET_CAPTURE_CREATION_ERROR_AUTOACTIVATION_FAILED = 0x02, ///< An error occurred while auto-activating the observer
    VU_MESH_AREA_TARGET_CAPTURE_CREATION_ERROR_FEATURE_NOT_SUPPORTED = 0x03, ///< Mesh functionality is not supported on the current device
    VU_MESH_AREA_TARGET_CAPTURE_CREATION_ERROR_INVALID_CAPTURE       = 0x04, ///< The provided capture is null or invalid
};

/// \brief Configuration for creating a Mesh observer using an Area Target Capture instance
typedef struct VuMeshAreaTargetCaptureConfig
{
    /// \brief Pointer to the Area Target Capture instance
    /**
     * The Area Target Capture instance is the exclusive source of the Mesh observations
     * that are reported by a Mesh Observer created with this configuration
     */
    VuAreaTargetCapture* capture;

    /// \brief Observer activation
    /**
     * \note The default value is VU_TRUE.
     */
    VuBool activate;
} VuMeshAreaTargetCaptureConfig;

/// \brief Default Mesh Observer configuration with Area Target Capture source
/**
 * \note Use this function to initialize the VuMeshAreaTargetCaptureConfig data structure with default values.
 */
VU_API VuMeshAreaTargetCaptureConfig VU_API_CALL vuMeshAreaTargetCaptureConfigDefault();

/// \brief Type identifier for Mesh observers
VU_CONST_INT(VU_OBSERVER_MESH_TYPE, 0xF);

/// \brief Type identifier for Mesh observations
VU_CONST_INT(VU_OBSERVATION_MESH_TYPE, 0xF);

/// \brief Create a Mesh observer with an Area Target Capture instance as source
/**
 * The Mesh observer will deliver Mesh observations that represent a mesh reconstruction of the environment
 * as provided by the specified Area Target Capture instance. Because the Mesh observer is dependent on the
 * existence of the Area Target Capture instance, one has to make sure to destroy the Mesh observer before
 * the Area Target Capture instance is destroyed.
 */
VU_API VuResult VU_API_CALL vuEngineCreateMeshObserverFromAreaTargetCaptureConfig(VuEngine* engine, VuObserver** observer, const VuMeshAreaTargetCaptureConfig* config, VuMeshAreaTargetCaptureCreationError* errorCode);

/// \brief Get all Mesh observers
VU_API VuResult VU_API_CALL vuEngineGetMeshObservers(const VuEngine* engine, VuObserverList *observerList);

/// \brief Get all Mesh observations from the Vuforia state
VU_API VuResult VU_API_CALL vuStateGetMeshObservations(const VuState* state, VuObservationList* list);

/// \brief The Mesh observation block represents a single self-contained mesh
/**
 * Each mesh block holds metadata and the actual mesh data. The metadata consists of a unique ID,
 * a timestamp, and a version number. These fields enable the unique identification of each mesh block
 * and the tracking of potential updates to the mesh block. Both the ID and the version are positive numbers.
 * The IDs are unique within a Vuforia session, they are generated at runtime and are not persistent across
 * Vuforia sessions. Moreover, the IDs are not reused, meaning that a newly added mesh block can never
 * have the same ID as an already removed mesh block. Whenever a mesh block has been updated, its
 * timestamp is adjusted to reflect the time of the change, and its version is increased.
 * The actual data of a mesh block consists of an axis aligned bounding box of the mesh, and a pointer to
 * a \ref VuMesh structure that holds the mesh.
 */
typedef struct VuMeshObservationBlock
{
    /// \brief Unique ID of the mesh block
    int32_t uuid;

    /// \brief Timestamp of the last update to the mesh block
    int64_t timestamp;

    /// \brief Current version of the mesh block, incremented each time it is updated
    int32_t version;

    /// \brief Axis-aligned bounding box of the observed mesh
    VuAABB bbox;

    /// \brief Mesh data
    VuMesh* mesh;
} VuMeshObservationBlock;

/// \brief List of Mesh observation blocks
typedef struct VuMeshObservationBlockList_ VuMeshObservationBlockList;

/// \brief Get number of elements in a Mesh observation block list
VU_API VuResult VU_API_CALL vuMeshObservationBlockListGetSize(const VuMeshObservationBlockList* list, int32_t* listSize);

/// \brief Get an element in a Mesh observation block list
VU_API VuResult VU_API_CALL vuMeshObservationBlockListGetElement(const VuMeshObservationBlockList* list, int32_t element, VuMeshObservationBlock* block);

/// \brief Mesh observation info, contains meshes and the respective transform into world space
/**
 * The Mesh observation info always holds a complete list of meshes. It includes meshes that have been
 * updated, recently added, and meshes that have remained unchanged. Consider meshes removed if they were
 * in the list in a previous observation but are no longer reported in the list of the current observation.
 */
typedef struct VuMeshObservationInfo
{
    /// \brief Model matrix to transform meshes to world space
    VuMatrix44F modelMatrix;

    /// \brief List of mesh blocks
    /**
     * \note The lifetime of the list is bound to the lifetime of the observation.
     */
    const VuMeshObservationBlockList* meshes;
} VuMeshObservationInfo;

/// \brief Get observation info associated with a Mesh observation
VU_API VuResult VU_API_CALL vuMeshObservationGetInfo(const VuObservation* observation, VuMeshObservationInfo* info);

/** \} */

#ifdef __cplusplus
}
#endif

#endif // _VU_MESHOBSERVER_H_
