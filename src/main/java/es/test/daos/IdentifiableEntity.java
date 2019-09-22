package es.test.daos;

public interface IdentifiableEntity<TId> {
    TId getId();

    void setId(TId id);
}
