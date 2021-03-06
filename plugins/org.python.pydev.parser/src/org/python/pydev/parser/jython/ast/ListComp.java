// Autogenerated AST node
package org.python.pydev.parser.jython.ast;
import org.python.pydev.parser.jython.SimpleNode;

public final class ListComp extends exprType implements comp_contextType {
    public exprType elt;
    public comprehensionType[] generators;
    public int ctx;

    public ListComp(exprType elt, comprehensionType[] generators, int ctx) {
        this.elt = elt;
        this.generators = generators;
        this.ctx = ctx;
    }

    public ListComp(exprType elt, comprehensionType[] generators, int ctx, SimpleNode parent) {
        this(elt, generators, ctx);
        this.beginLine = parent.beginLine;
        this.beginColumn = parent.beginColumn;
    }

    public ListComp createCopy() {
        comprehensionType[] new0;
        if(this.generators != null){
        new0 = new comprehensionType[this.generators.length];
        for(int i=0;i<this.generators.length;i++){
            new0[i] = (comprehensionType) (this.generators[i] != null?
            this.generators[i].createCopy():null);
        }
        }else{
            new0 = this.generators;
        }
        ListComp temp = new ListComp(elt!=null?(exprType)elt.createCopy():null, new0, ctx);
        temp.beginLine = this.beginLine;
        temp.beginColumn = this.beginColumn;
        if(this.specialsBefore != null){
            for(Object o:this.specialsBefore){
                if(o instanceof commentType){
                    commentType commentType = (commentType) o;
                    temp.getSpecialsBefore().add(commentType.createCopy());
                }
            }
        }
        if(this.specialsAfter != null){
            for(Object o:this.specialsAfter){
                if(o instanceof commentType){
                    commentType commentType = (commentType) o;
                    temp.getSpecialsAfter().add(commentType.createCopy());
                }
            }
        }
        return temp;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("ListComp[");
        sb.append("elt=");
        sb.append(dumpThis(this.elt));
        sb.append(", ");
        sb.append("generators=");
        sb.append(dumpThis(this.generators));
        sb.append(", ");
        sb.append("ctx=");
        sb.append(dumpThis(this.ctx, comp_contextType.comp_contextTypeNames));
        sb.append("]");
        return sb.toString();
    }

    public Object accept(VisitorIF visitor) throws Exception {
        return visitor.visitListComp(this);
    }

    public void traverse(VisitorIF visitor) throws Exception {
        if (elt != null){
            elt.accept(visitor);
        }
        if (generators != null) {
            for (int i = 0; i < generators.length; i++) {
                if (generators[i] != null){
                    generators[i].accept(visitor);
                }
            }
        }
    }

}
