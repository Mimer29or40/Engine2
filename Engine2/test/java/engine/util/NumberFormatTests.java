package engine.util;

import rutils.NumUtil;
import rutils.group.IPairI;

import static rutils.StringUtil.println;

public class NumberFormatTests
{
    public static void main(String[] args)
    {
        double[] numbers = new double[] {
                100, -100, 0.123, 0.1, 12345, -1.34234
        };
    
        IPairI format = NumUtil.getFormatNumbers(numbers);
        
        for (double number : numbers)
        {
            println(NumUtil.format(number, format));
        }
    }
}
