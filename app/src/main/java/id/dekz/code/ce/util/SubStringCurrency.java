package id.dekz.code.ce.util;

/**
 * Created by DEKZ on 2/11/2016.
 */
public class SubStringCurrency {

    private String amount;

    public SubStringCurrency(String amount) {
        this.amount = amount;
    }

    public String getFormattedAmount(String amount){
        String result = "";
        String decimal = "";
        int length = amount.length() - 3;
        String word[] = new String[length/3];
        if(length==15){
            //ratusan triliun
            int start=0;
            int end=3;
            decimal = amount.substring(length,length+end);
            for(int w=0;w<word.length;w++){
                word[w] = amount.substring(start,end);
                if(w==0){
                    result = result+word[w];
                }else{
                    result = result+","+word[w];
                }
                start = start+3;
                end = end+3;
            }
        }
        return result+decimal;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
