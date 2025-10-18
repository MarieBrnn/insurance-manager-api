package com.barenne.insurance_manager_api.utils;

public class Constants {
    public static final String FIND_ACTIVE_CONTRACTS_BY_CLIENT_ID =
            "SELECT c FROM Contract c WHERE c.client.id = :clientId AND (c.endDate IS NULL OR c.endDate > :currentDate)";

    public static final String FIND_ACTIVE_CONTRACTS_BY_CLIENT_ID_FILTERED_BY_UPDATEDATE =
            "SELECT c FROM Contract c WHERE c.client.id = :clientId AND (c.endDate IS NULL OR c.endDate > :currentDate) AND c.updatedAt >= :updateDate";

    public static final String SUM_COST_AMOUNT_ACTIVE_CONTRACTS_BY_CLIENT_ID =
            "SELECT SUM(c.costAmount) FROM Contract c WHERE c.client.id= :clientId AND (c.endDate IS NULL OR c.endDate > :currentDate)";

    public static final String UPDATE_ALL_CONTRACTS_ENDDATE_BY_CLIENT_ID =
            "UPDATE Contract c SET c.endDate = :endDate WHERE c.client.id = :clientId AND (c.endDate IS NULL OR c.endDate > : currentDate)";
}
