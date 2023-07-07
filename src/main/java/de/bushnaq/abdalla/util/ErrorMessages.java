package de.bushnaq.abdalla.util;

public interface ErrorMessages {
    String ERROR_001_PERSON_IS_NOT_VISIBLE = "Error #001: %s is not visible. A person is visible if he is member of the family or has children with someone that is member of the family.";
    String ERROR_002_PAGE_INDEX_NULL = "Error #002. %s Page index null";
    String ERROR_003_PERSON_MISSING_FIRST_NAME = "Error #003. %s Person missing first name.";
    String ERROR_004_PERSON_MISSING_LAST_NAME = "Error #004. %s Person missing last name.";
    String ERROR_005_PERSON_UNKNOWN_ORIGINS = "Error #005. %s is member of %s with unknown origins.";
    String ERROR_006_OVERLAPPING = "Error #006. %s is overlapping with %s.";
    String ERROR_101_COLUMN_S_IS_MISSING = "Error #101. Column %s is missing.";
    String ERROR_102_UNKNOWN_HEADER = "Error #102. Unknown header '%s' at column '%s'.";
}
