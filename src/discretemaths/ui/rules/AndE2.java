package discretemaths.ui.rules;

import discretemaths.Proof;
import discretemaths.forms.Form;
import discretemaths.rules.InvalidRuleException;

public class AndE2 extends Rule{
    @Override
    public Proof addRule(Form f, Proof p, String args) throws InvalidRuleException, InvalidArgsException {
        int num = 0;
        try{
            num = Integer.parseInt(args);
        }catch(Exception e){
            if(args.isBlank()){
                throw new InvalidArgsException("No arguments were given");
            }
            throw new InvalidArgsException("Invalid arguments entered (" + args + ")");
        }
        p.andE2(num);
        return p;
    }
}
