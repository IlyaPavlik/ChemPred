package ru.pavlik.chempred.server.utils;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import ru.pavlik.chempred.client.model.LinkType;
import ru.pavlik.chempred.client.model.dao.ElementDao;
import ru.pavlik.chempred.client.model.dao.LinkDao;
import ru.pavlik.chempred.client.model.dao.StructureDao;
import ru.pavlik.chempred.server.model.converter.AtomContainerConverter;

import java.util.ArrayList;
import java.util.List;

public class SmilesUtils {

    private SmilesUtils() {
        throw new UnsupportedOperationException("Utils constructor unsupported");
    }

    public static StructureDao parseSmiles(List<ElementDao> sourceElements, String smiles) {
        List<LinkDao> links = new ArrayList<>();
        List<ElementDao> elements = new ArrayList<>();
        List<String> smilesSymbols = new ArrayList<>();
        StructureDao structure = new StructureDao();

        //Распознавание элементов, связей
        for (int i = 0; i < smiles.length(); i++) {
            String temp = "";
            char symbol = smiles.charAt(i);

            if (Character.isUpperCase(symbol)) {
                temp = String.valueOf(symbol);
            }

            if (i + 1 < smiles.length() && Character.isLowerCase(smiles.charAt(i + 1))) {
                temp += smiles.charAt(i + 1);
            }

            if (isElement(sourceElements, temp)) {
                smilesSymbols.add(temp);
            }

            if (isCorrectSymbol(symbol)) {
                smilesSymbols.add(String.valueOf(symbol));
            }
        }

        //Создание элементов и связей между ними
        String linkType = "";
        List<Integer> loopElements = new ArrayList<>();
        ElementDao sourceElement = null, targetElement = null;
        int cr = 0;
        String loopIteration = "";
        boolean loop = false;

        for (int i = 0; i < smilesSymbols.size(); i++) {
            if ((i + 1) < smilesSymbols.size()
                    && smilesSymbols.get(i + 1).equals(")") && (i - 1) >= 0
                    && !smilesSymbols.get(i - 1).equals("(")) {
                loopElements.remove(loopElements.size() - 1);
                sourceElement = elements.get(loopElements.get(loopElements.size() - 1));
            }

            if (isElement(sourceElements, smilesSymbols.get(i))
                    && ((i + 1) < smilesSymbols.size() && !smilesSymbols.get(i + 1).equals(")")
                    && (!isElement(sourceElements, smilesSymbols.get(i + 1)) || (i - 1) >= 0
                    && smilesSymbols.get(i - 1).equals("("))) || i == 0) {

                if ((i - 1) >= 0 && smilesSymbols.get(i).equals(elements.get(elements.size() - 1).getSymbol())) {
                    sourceElement = elements.get(elements.size() - 1);
                } else {
                    sourceElement = getElement(sourceElements, smilesSymbols.get(i));
                    elements.add(sourceElement);

                }
                loopElements.add(elements.size() - 1);
            }

            if (smilesSymbols.get(i).equals("=") || smilesSymbols.get(i).equals("#")) {
                linkType = smilesSymbols.get(i);
            }

            if ((i + 1) < smilesSymbols.size() && isElement(sourceElements, smilesSymbols.get(i + 1))) {
                targetElement = getElement(sourceElements, smilesSymbols.get(i + 1));
                elements.add(targetElement);

                switch (linkType) {
                    case "=":
                        links.add(new LinkDao(sourceElement, targetElement, LinkType.DOUBLE));
                        break;
                    case "#":
                        links.add(new LinkDao(sourceElement, targetElement, LinkType.TRIPLE));
                        break;
                    default:
                        links.add(new LinkDao(sourceElement, targetElement, LinkType.SINGLE));
                        break;
                }

                linkType = "";

                if ((i + 2) < smilesSymbols.size() && !smilesSymbols.get(i + 2).equals(")")) {
                    sourceElement = targetElement;
                } else {
                    sourceElement = elements.get(loopElements.get(loopElements.size() - 1));
                }
            }

            if ((i + 1) < smilesSymbols.size() && smilesSymbols.get(i + 1).equals(loopIteration) && loop) {
                sourceElement = elements.get(cr);
                switch (linkType) {
                    case "=":
                        links.add(new LinkDao(sourceElement, targetElement, LinkType.DOUBLE));
                        break;
                    case "#":
                        links.add(new LinkDao(sourceElement, targetElement, LinkType.TRIPLE));
                        break;
                    default:
                        links.add(new LinkDao(sourceElement, targetElement, LinkType.SINGLE));
                        break;
                }
                loop = false;
                loopIteration = "";
                sourceElement = targetElement;
                continue;
            }

            if (!loop && (i + 1) < smilesSymbols.size()
                    && (smilesSymbols.get(i + 1).equals("1") || smilesSymbols.get(i + 1).equals("2"))) {
                cr = elements.size() - 1;
                loop = true;
                loopIteration = smilesSymbols.get(i + 1);
            }
        }
        structure.setLinks(links);
        structure.setElements(elements);

        return structure;
    }

    public static String parseStructure(StructureDao structureDao) {
        SmilesGenerator smilesGenerator = new SmilesGenerator(SmiFlavor.Generic);
        AtomContainerConverter atomContainerConverter = new AtomContainerConverter();
        IAtomContainer atomContainer = atomContainerConverter.convertToDB(structureDao);
        try {
            return smilesGenerator.create(atomContainer);
        } catch (CDKException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Deprecated
    public static String parseStructure(List<LinkDao> links) {
        String resultSmiles = links.get(0).getElementSource().getSymbol();

        for (int i = 0; i < links.size(); i++) {
            boolean nextEl = containsNextElement(links, i);

            if (nextEl) resultSmiles += "(";
            switch (links.get(i).getLinkType()) {
                case DOUBLE:
                    resultSmiles += "=";
                    break;
                case TRIPLE:
                    resultSmiles += "#";
                    break;
            }

            resultSmiles += links.get(i).getElementTarget().getSymbol();

            if (nextEl) resultSmiles += ")";
        }

        return resultSmiles;
    }

    private static boolean containsNextElement(List<LinkDao> links, int startIndex) {
        if (startIndex + 1 > links.size()) return false;

        ElementDao sourceElement = links.get(startIndex).getElementSource();
        for (int i = startIndex + 1; i < links.size(); i++) {
            if (sourceElement == links.get(i).getElementSource()) {
                return true;
            }
        }

        return false;
    }

    private static boolean isCorrectSymbol(char symbol) {
        return (symbol == ')' || symbol == '(')
                || (symbol == '=' || symbol == '#')
                || (symbol > '0' && symbol < '9');
    }

    private static boolean isElement(List<ElementDao> sourceElements, String symbol) {
        return getElement(sourceElements, symbol) != null;
    }

    private static ElementDao getElement(List<ElementDao> sourceElements, String symbol) {
        for (ElementDao sourceElement : sourceElements) {
            if (sourceElement.getSymbol().equals(symbol)) {
                return new ElementDao(sourceElement);
            }
        }
        return null;
    }

}
