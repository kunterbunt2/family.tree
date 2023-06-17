package de.bushnaq.abdalla.util;

public interface ErrorMessages {
    String ERROR_001_PERSON_IS_NOT_VISIBLE = "Error #001: [%d][%d]%s %s is not visible. A person is visible if he is member of the family or has children with someone from the family.";
    String ERROR_002_PAGE_INDEX_NULL = "Error #002. Page index null";
    String ERROR_003_PERSON_MISSING_FIRST_NAME = "Error #003. Person missing first name.";
    String ERROR_004_PERSON_MISSING_LAST_NAME = "Error #004. Person missing last name.";
    String ERROR_005_PERSON_UNKNOWN_ORIGINS = "Error #005. A %s member with unknown origins.";
    String ERROR_006_OVERLAPPING = "Error #006. [%d][%d]%s %s is overlapping with [%d][%d]%s %s.";
    String ERROR_101_COLUMN_S_IS_MISSING = "Error #101. Column %s is missing.";
    String ERROR_102_UNKNOWN_HEADER = "Error #102. Unknown header '%s' at column '%s'.";
}
