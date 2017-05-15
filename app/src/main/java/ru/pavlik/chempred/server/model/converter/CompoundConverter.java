package ru.pavlik.chempred.server.model.converter;

import ru.pavlik.chempred.client.model.dao.CompoundDao;
import ru.pavlik.chempred.server.model.Compound;

public class CompoundConverter extends BaseConverter<CompoundDao, Compound> {

    @Override
    public CompoundDao convertToDao(Compound compound) {
        CompoundDao compoundDao = new CompoundDao();
        compoundDao.setId(compound.getId());
        compoundDao.setName(compound.getName());
        compoundDao.setBrutto(compound.getBrutto());
        compoundDao.setSmiles(compound.getSmiles());
        compoundDao.setLowFactor(compound.getExperimentalFactor());
        return compoundDao;
    }

    @Override
    public Compound convertToDB(CompoundDao compoundDao) {
        Compound compound = new Compound();
        compound.setId(compoundDao.getId());
        compound.setName(compoundDao.getName());
        compound.setBrutto(compoundDao.getBrutto());
        compound.setSmiles(compoundDao.getSmiles());
        compound.setPredictionFactor(compoundDao.getLowFactor());
        return compound;
    }
}
