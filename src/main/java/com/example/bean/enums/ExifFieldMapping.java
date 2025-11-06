package com.example.bean.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author WuQinglong
 * @date 2025/10/31 10:05
 */
@Getter
@AllArgsConstructor
public enum ExifFieldMapping {
    EXIF_TOOL_VERSION_NUMBER("exifToolVersionNumber", "ExifTool Version Number"),
    FILE_NAME("fileName", "File Name"),
    DIRECTORY("directory", "Directory"),
    FILE_SIZE("fileSize", "File Size"),
    FILE_MODIFICATION_DATE_TIME("fileModificationDateTime", "File Modification Date/Time"),
    FILE_ACCESS_DATE_TIME("fileAccessDateTime", "File Access Date/Time"),
    FILE_INODE_CHANGE_DATE_TIME("fileInodeChangeDateTime", "File Inode Change Date/Time"),
    FILE_PERMISSIONS("filePermissions", "File Permissions"),
    FILE_TYPE("fileType", "File Type"),
    FILE_TYPE_EXTENSION("fileTypeExtension", "File Type Extension"),
    MIME_TYPE("mimeType", "MIME Type"),
    MAJOR_BRAND("majorBrand", "Major Brand"),
    MINOR_VERSION("minorVersion", "Minor Version"),
    COMPATIBLE_BRANDS("compatibleBrands", "Compatible Brands"),
    HANDLER_TYPE("handlerType", "Handler Type"),
    PRIMARY_ITEM_REFERENCE("primaryItemReference", "Primary Item Reference"),
    META_IMAGE_SIZE("metaImageSize", "Meta Image Size"),
    EXIF_BYTE_ORDER("exifByteOrder", "Exif Byte Order"),
    MAKE("make", "Make"),
    CAMERA_MODEL_NAME("cameraModelName", "Camera Model Name"),
    ORIENTATION("orientation", "Orientation"),
    X_RESOLUTION("xResolution", "X Resolution"),
    Y_RESOLUTION("yResolution", "Y Resolution"),
    RESOLUTION_UNIT("resolutionUnit", "Resolution Unit"),
    MODIFY_DATE("modifyDate", "Modify Date"),
    Y_CB_CR_POSITIONING("yCbCrPositioning", "Y Cb Cr Positioning"),
    INTEROPERABILITY_INDEX("interoperabilityIndex", "Interoperability Index"),
    INTEROPERABILITY_VERSION("interoperabilityVersion", "Interoperability Version"),
    EXPOSURE_TIME("exposureTime", "Exposure Time"),
    F_NUMBER("fNumber", "F Number"),
    EXPOSURE_PROGRAM("exposureProgram", "Exposure Program"),
    ISO("iso", "ISO"),
    EXIF_VERSION("exifVersion", "Exif Version"),
    DATE_TIME_ORIGINAL("dateTimeOriginal", "Date/Time Original"),
    CREATE_DATE("createDate", "Create Date"),
    OFFSET_TIME_ORIGINAL("offsetTimeOriginal", "Offset Time Original"),
    COMPONENTS_CONFIGURATION("componentsConfiguration", "Components Configuration"),
    SHUTTER_SPEED_VALUE("shutterSpeedValue", "Shutter Speed Value"),
    APERTURE_VALUE("apertureValue", "Aperture Value"),
    BRIGHTNESS_VALUE("brightnessValue", "Brightness Value"),
    EXPOSURE_COMPENSATION("exposureCompensation", "Exposure Compensation"),
    MAX_APERTURE_VALUE("maxApertureValue", "Max Aperture Value"),
    METERING_MODE("meteringMode", "Metering Mode"),
    FLASH("flash", "Flash"),
    FOCAL_LENGTH("focalLength", "Focal Length"),
    MAKER_NOTE_UNKNOWN_TEXT("makerNoteUnknownText", "Maker Note Unknown Text"),
    USER_COMMENT("userComment", "User Comment"),
    SUB_SEC_TIME("subSecTime", "Sub Sec Time"),
    SUB_SEC_TIME_ORIGINAL("subSecTimeOriginal", "Sub Sec Time Original"),
    SUB_SEC_TIME_DIGITIZED("subSecTimeDigitized", "Sub Sec Time Digitized"),
    FLASHPIX_VERSION("flashpixVersion", "Flashpix Version"),
    COLOR_SPACE("colorSpace", "Color Space"),
    EXIF_IMAGE_WIDTH("exifImageWidth", "Exif Image Width"),
    EXIF_IMAGE_HEIGHT("exifImageHeight", "Exif Image Height"),
    SENSING_METHOD("sensingMethod", "Sensing Method"),
    SCENE_TYPE("sceneType", "Scene Type"),
    EXPOSURE_MODE("exposureMode", "Exposure Mode"),
    WHITE_BALANCE("whiteBalance", "White Balance"),
    DIGITAL_ZOOM_RATIO("digitalZoomRatio", "Digital Zoom Ratio"),
    FOCAL_LENGTH_IN_35MM_FORMAT("focalLengthIn35mmFormat", "Focal Length In 35mm Format"),
    SCENE_CAPTURE_TYPE("sceneCaptureType", "Scene Capture Type"),
    LENS_MODEL("lensModel", "Lens Model"),
    GPS_LATITUDE_REF("gpsLatitudeRef", "GPS Latitude Ref"),
    GPS_LONGITUDE_REF("gpsLongitudeRef", "GPS Longitude Ref"),
    GPS_ALTITUDE_REF("gpsAltitudeRef", "GPS Altitude Ref"),
    GPS_TIME_STAMP("gpsTimeStamp", "GPS Time Stamp"),
    GPS_DATE_STAMP("gpsDateStamp", "GPS Date Stamp"),
    COMPRESSION("compression", "Compression"),
    THUMBNAIL_OFFSET("thumbnailOffset", "Thumbnail Offset"),
    THUMBNAIL_LENGTH("thumbnailLength", "Thumbnail Length"),
    PROFILE_CMM_TYPE("profileCmmType", "Profile CMM Type"),
    PROFILE_VERSION("profileVersion", "Profile Version"),
    PROFILE_CLASS("profileClass", "Profile Class"),
    COLOR_SPACE_DATA("colorSpaceData", "Color Space Data"),
    PROFILE_CONNECTION_SPACE("profileConnectionSpace", "Profile Connection Space"),
    PROFILE_DATE_TIME("profileDateTime", "Profile Date Time"),
    PROFILE_FILE_SIGNATURE("profileFileSignature", "Profile File Signature"),
    PRIMARY_PLATFORM("primaryPlatform", "Primary Platform"),
    CMM_FLAGS("cmmFlags", "CMM Flags"),
    DEVICE_MANUFACTURER("deviceManufacturer", "Device Manufacturer"),
    DEVICE_MODEL("deviceModel", "Device Model"),
    DEVICE_ATTRIBUTES("deviceAttributes", "Device Attributes"),
    RENDERING_INTENT("renderingIntent", "Rendering Intent"),
    CONNECTION_SPACE_ILLUMINANT("connectionSpaceIlluminant", "Connection Space Illuminant"),
    PROFILE_CREATOR("profileCreator", "Profile Creator"),
    PROFILE_ID("profileId", "Profile ID"),
    PROFILE_DESCRIPTION("profileDescription", "Profile Description"),
    PROFILE_COPYRIGHT("profileCopyright", "Profile Copyright"),
    MEDIA_WHITE_POINT("mediaWhitePoint", "Media White Point"),
    RED_MATRIX_COLUMN("redMatrixColumn", "Red Matrix Column"),
    GREEN_MATRIX_COLUMN("greenMatrixColumn", "Green Matrix Column"),
    BLUE_MATRIX_COLUMN("blueMatrixColumn", "Blue Matrix Column"),
    RED_TONE_REPRODUCTION_CURVE("redToneReproductionCurve", "Red Tone Reproduction Curve"),
    CHROMATIC_ADAPTATION("chromaticAdaptation", "Chromatic Adaptation"),
    BLUE_TONE_REPRODUCTION_CURVE("blueToneReproductionCurve", "Blue Tone Reproduction Curve"),
    GREEN_TONE_REPRODUCTION_CURVE("greenToneReproductionCurve", "Green Tone Reproduction Curve"),
    HEVC_CONFIGURATION_VERSION("hevcConfigurationVersion", "HEVC Configuration Version"),
    GENERAL_PROFILE_SPACE("generalProfileSpace", "General Profile Space"),
    GENERAL_TIER_FLAG("generalTierFlag", "General Tier Flag"),
    GENERAL_PROFILE_IDC("generalProfileIdc", "General Profile IDC"),
    GEN_PROFILE_COMPATIBILITY_FLAGS("genProfileCompatibilityFlags", "Gen Profile Compatibility Flags"),
    CONSTRAINT_INDICATOR_FLAGS("constraintIndicatorFlags", "Constraint Indicator Flags"),
    GENERAL_LEVEL_IDC("generalLevelIdc", "General Level IDC"),
    MIN_SPATIAL_SEGMENTATION_IDC("minSpatialSegmentationIdc", "Min Spatial Segmentation IDC"),
    PARALLELISM_TYPE("parallelismType", "Parallelism Type"),
    CHROMA_FORMAT("chromaFormat", "Chroma Format"),
    BIT_DEPTH_LUMA("bitDepthLuma", "Bit Depth Luma"),
    BIT_DEPTH_CHROMA("bitDepthChroma", "Bit Depth Chroma"),
    AVERAGE_FRAME_RATE("averageFrameRate", "Average Frame Rate"),
    CONSTANT_FRAME_RATE("constantFrameRate", "Constant Frame Rate"),
    NUM_TEMPORAL_LAYERS("numTemporalLayers", "Num Temporal Layers"),
    TEMPORAL_ID_NESTED("temporalIdNested", "Temporal ID Nested"),
    IMAGE_WIDTH("imageWidth", "Image Width"),
    IMAGE_HEIGHT("imageHeight", "Image Height"),
    IMAGE_SPATIAL_EXTENT("imageSpatialExtent", "Image Spatial Extent"),
    MEDIA_DATA_SIZE("mediaDataSize", "Media Data Size"),
    MEDIA_DATA_OFFSET("mediaDataOffset", "Media Data Offset"),
    APERTURE("aperture", "Aperture"),
    IMAGE_SIZE("imageSize", "Image Size"),
    MEGAPIXELS("megapixels", "Megapixels"),
    SCALE_FACTOR_TO_35_MM_EQUIVALENT("scaleFactorTo35MmEquivalent", "Scale Factor To 35 mm Equivalent"),
    SHUTTER_SPEED("shutterSpeed", "Shutter Speed"),
    THUMBNAIL_IMAGE("thumbnailImage", "Thumbnail Image"),
    GPS_ALTITUDE("gpsAltitude", "GPS Altitude"),
    GPS_DATE_TIME("gpsDateTime", "GPS Date/Time"),
    GPS_LATITUDE("gpsLatitude", "GPS Latitude"),
    GPS_LONGITUDE("gpsLongitude", "GPS Longitude"),
    CIRCLE_OF_CONFUSION("circleOfConfusion", "Circle Of Confusion"),
    FIELD_OF_VIEW("fieldOfView", "Field Of View"),
    GPS_POSITION("gpsPosition", "GPS Position"),
    HYPERFOCAL_DISTANCE("hyperfocalDistance", "Hyperfocal Distance"),
    LIGHT_VALUE("lightValue", "Light Value"),
    LENS_ID("lensId", "Lens ID");

    private final String fieldName;
    private final String exifKey;

    /**
     * 根据字段名获取对应的Exif键名
     */
    public static String getExifKeyByFieldName(String fieldName) {
        for (ExifFieldMapping mapping : values()) {
            if (mapping.fieldName.equals(fieldName)) {
                return mapping.exifKey;
            }
        }
        return null;
    }

    /**
     * 根据Exif键名获取对应的字段名
     */
    public static String getFieldNameByExifKey(String exifKey) {
        for (ExifFieldMapping mapping : values()) {
            if (mapping.exifKey.equals(exifKey)) {
                return mapping.fieldName;
            }
        }
        return null;
    }
}
