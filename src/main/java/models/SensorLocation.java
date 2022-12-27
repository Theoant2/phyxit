package models;

import java.util.Objects;

public interface SensorLocation {

    public String getLocationName();

    public static SensorLocation simple(String location) { return new Simple(location); }

    public static class Simple implements SensorLocation {
        private String location;

        public Simple(String location) {
            this.location = location;
        }

        @Override
        public String getLocationName() {
            return this.location;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Simple simple = (Simple) o;
            return Objects.equals(location, simple.location);
        }

        @Override
        public int hashCode() {
            return Objects.hash(location);
        }
    }

}
