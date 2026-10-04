
/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.db; // Ajusta el paquete según tu proyecto

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Persister personalizado para ORMLite.
 */
public class LocalDatePersister extends BaseDataType {


    private static final LocalDatePersister SINGLETON = new LocalDatePersister();


    public static LocalDatePersister getSingleton() {
        return SINGLETON;
    }

    // 3. Constructor privado
    private LocalDatePersister() {
        // Super recibe: Tipo de dato en SQL (ej: STRING) y un arreglo de las clases Java que maneja
        super(SqlType.STRING, new Class<?>[]{LocalDate.class});
    }

    @Override
    public Object parseDefaultString(FieldType fieldType, String defaultStr) {
        // Convierte el valor por defecto (String) al objeto Java
        return LocalDate.parse(defaultStr);
    }

    @Override
    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        // Extrae el dato primitivo desde los resultados de la DB
        return results.getString(columnPos);
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        // Convierte el dato SQL (String en este caso) al objeto Java (LocalDate)
        if (sqlArg == null) {
            return null;
        }
        return LocalDate.parse((String) sqlArg);
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        // Convierte el objeto Java al formato SQL (String) para guardarlo en la DB
        if (javaObject == null) {
            return null;
        }
        return ((LocalDate) javaObject).toString();
    }
}
