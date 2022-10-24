package discretemaths.ui.rules;

import discretemaths.Proof;
import discretemaths.forms.Form;
import discretemaths.rules.InvalidRuleException;

public class ImpliesE extends Rule {
    @Override
    public Proof addRule(Form f, Proof p, String args) throws InvalidRuleException, InvalidArgsException {
        int num1 = 0;
        int num2 = 0;
        String[] split = args.split(",");
        if(split.length != 2)
            throw new InvalidArgsException("Invalid arguments entered (" + args + ")");

        try{
            num1 = Integer.parseInt(split[0]);
            num2 = Integer.parseInt(split[1]);
        }catch(Exception e){
            if(args.isBlank()){
                throw new InvalidArgsException("No arguments were given");
            }
            throw new InvalidArgsException("Invalid arguments entered (" + args + ")");
        }
        
        p.impliesE(num1, num2);

        return p;
    }
}
