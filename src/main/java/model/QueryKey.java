package model;

public enum QueryKey {
    userId,
    password,
    name,
    email;

    public static QueryKey from(String queryKey) {
        for (QueryKey m : values()) {
            if (m.name().equalsIgnoreCase(queryKey)) {
                return m;
            }
        }
        throw new IllegalArgumentException("Unknown key: " + queryKey);
    }
}
