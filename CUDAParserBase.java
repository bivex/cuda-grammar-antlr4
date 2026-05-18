import org.antlr.v4.runtime.*;

public abstract class CUDAParserBase extends Parser
{
    protected CUDAParserBase(TokenStream input)
    {
        super(input);
    }

    protected boolean IsPureSpecifierAllowed()
    {
	try
	{
	    var x = this._ctx; // memberDeclarator
	    var c = x.getChild(0).getChild(0);
	    var c2 = c.getChild(0);
	    var p = c2.getChild(1);
	    if (p == null) return false;
	    return (p instanceof CUDAParser.ParametersAndQualifiersContext);
	}
	catch (Exception e)
	{
	}
	return false;
    }
}
