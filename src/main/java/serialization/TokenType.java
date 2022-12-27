package serialization;

import java.util.Objects;

public class TokenType<T, K> {

    private Class<T> mainClass;
    private Class<K> parameterClass;

    private TokenType(Class<T> mainClass, Class<K> parameterClass) {
        this.mainClass = mainClass;
        this.parameterClass = parameterClass;
    }

    private TokenType(T object, Class<K> parameterClass) {
        this.mainClass = (Class<T>) object.getClass();
        this.parameterClass = parameterClass;
    }

    public Class<T> getMainClass() {
        return mainClass;
    }

    public Class<K> getParameterClass() {
        return parameterClass;
    }

    public static <T> TokenType<T, Object> wrap(Class<T> clazz) {
        return new TokenType<>(clazz, Object.class);
    }

    public static <T> TokenType<T, Object> wrap(T object) {
        return new TokenType<>(object, Object.class);
    }

    public static <T, K> TokenType<T, K> from(Class<T> mainClass, Class<K> parameterClass) {
        return new TokenType<>(mainClass, parameterClass);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenType<?, ?> tokenType = (TokenType<?, ?>) o;
        return Objects.equals(mainClass, tokenType.mainClass) && Objects.equals(parameterClass, tokenType.parameterClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mainClass, parameterClass);
    }

    @Override
    public String toString() {
        return mainClass.getName() + "<" + parameterClass.getName() + ">";
    }

}
