package ru.pavlik.chempred.server.utils;

import org.openscience.cdk.Atom;
import org.openscience.cdk.formula.MolecularFormula;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

public class BruttoUtils {

    private BruttoUtils() {
        throw new UnsupportedOperationException("Utils constructor unsupported");
    }

    public static String bruttoFromSmiles(String smiles) {
        IAtomContainer atomContainer = CdkUtils.getAtomContainer(smiles);
        MolecularFormula molecularFormula = new MolecularFormula();
        if (atomContainer != null) {
            for (IAtom atom : atomContainer.atoms()) {
                molecularFormula.addIsotope(atom);
                Integer hydrogenCount = atom.getImplicitHydrogenCount();
                if (hydrogenCount > 0) {
                    molecularFormula.addIsotope(new Atom("H"), hydrogenCount);
                }
            }
        }
        return MolecularFormulaManipulator.getString(molecularFormula);
    }

}
