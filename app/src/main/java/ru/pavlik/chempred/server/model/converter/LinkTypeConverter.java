package ru.pavlik.chempred.server.model.converter;

import org.openscience.cdk.interfaces.IBond;
import ru.pavlik.chempred.client.model.LinkType;

public class LinkTypeConverter extends BaseConverter<LinkType, IBond.Order> {

    @Override
    public LinkType convertToDao(IBond.Order order) {
        switch (order) {
            case SINGLE:
                return LinkType.SINGLE;
            case DOUBLE:
                return LinkType.DOUBLE;
            case TRIPLE:
                return LinkType.TRIPLE;
            case QUADRUPLE:
            case QUINTUPLE:
            case SEXTUPLE:
            case UNSET:
            default:
                throw new IllegalArgumentException("Illegal link type: " + order);
        }
    }

    @Override
    public IBond.Order convertToDB(LinkType linkType) {
        switch (linkType) {
            case SINGLE:
                return IBond.Order.SINGLE;
            case DOUBLE:
                return IBond.Order.DOUBLE;
            case TRIPLE:
                return IBond.Order.TRIPLE;
            case TOP:
            case DOWN:
            default:
                throw new IllegalArgumentException("Illegal link type: " + linkType);
        }
    }
}
