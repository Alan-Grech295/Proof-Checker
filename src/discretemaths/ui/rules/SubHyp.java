package discretemaths.ui.rules;

import discretemaths.Proof;
import discretemaths.forms.Form;
import discretemaths.rules.InvalidRuleException;

public class SubHyp extends Rule{

    @Override
    public Proof addRule(Form f, Proof p, String args) throws InvalidRuleException, InvalidArgsException{
        p.subhyp(f);

        return p;
    }
}
