package ru.pavlik.chempred.client.model.converter;

public abstract class BaseConverter<DAOModel, NativeModel> {

    public abstract DAOModel convertToDao(NativeModel nativeModel);

    public abstract NativeModel convertToNative(DAOModel daoModel);
}
