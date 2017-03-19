package ru.pavlik.chempred.server.model.converter;

public abstract class BaseConverter<DAOModel, DBModel> {

    public abstract DAOModel convertToDao(DBModel dbModel);

    public abstract DBModel convertToDB(DAOModel daoModel);
}
