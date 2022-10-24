package discretemaths.ui.rules;

import discretemaths.Proof;
import discretemaths.forms.Form;
import discretemaths.rules.InvalidRuleException;

import static discretemaths.Proof.hyp;

public class Hyp extends Rule{

    @Override
    public Proof addRule(Form f, Proof p, String args) throws InvalidRuleException, InvalidArgsException{
        p = hyp(f);

        return p;
    }
}
