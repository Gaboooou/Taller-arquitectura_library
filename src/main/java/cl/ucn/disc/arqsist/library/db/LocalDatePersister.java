/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.db;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Custom persister for LocalDate in ORMLite.
 */
public class LocalDatePersister extends BaseDataType {

    /**
     * The constant SINGLETON.
     */
    private static final LocalDatePersister SINGLETON = new LocalDatePersister();

    /**
     * Gets singleton.
     *
     * @return the singleton
     */
    public static LocalDatePersister getSingleton() {
        return SINGLETON;
    }

    /**
     * Instantiates a new Local date persister.
     */
    private LocalDatePersister() {
        super(SqlType.STRING, new Class<?>[]{LocalDate.class});
    }

    /**
     * Parse default string object.
     *
     * @param fieldType  the field type
     * @param defaultStr the default str
     * @return the object
     */
    @Override
    public Object parseDefaultString(FieldType fieldType, String defaultStr) {
        return LocalDate.parse(defaultStr);
    }

    /**
     * Result to sql arg object.
     *
     * @param fieldType the field type
     * @param results   the results
     * @param columnPos the column pos
     * @return the object
     * @throws SQLException the sql exception
     */
    @Override
    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        return results.getString(columnPos);
    }

    /**
     * Sql arg to java object.
     *
     * @param fieldType the field type
     * @param sqlArg    the sql arg
     * @param columnPos the column pos
     * @return the object
     */
    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        if (sqlArg == null) {
            return null;
        }
        return LocalDate.parse((String) sqlArg);
    }

    /**
     * Java to sql arg object.
     *
     * @param fieldType  the field type
     * @param javaObject the java object
     * @return the object
     */
    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        if (javaObject == null) {
            return null;
        }
        return ((LocalDate) javaObject).toString();
    }
}
