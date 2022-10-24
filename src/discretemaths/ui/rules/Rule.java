package discretemaths.ui.rules;

import discretemaths.Proof;
import discretemaths.forms.Form;
import discretemaths.rules.InvalidRuleException;


public abstract class Rule {
    public Rule(){}

    public abstract Proof addRule(Form f, Proof p, String args) throws InvalidRuleException, InvalidArgsException;
}
