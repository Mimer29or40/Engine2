package engine.util;

import static engine.util.Util.println;

public class NumberFormatTests
{
    public static void main(String[] args)
    {
        double[] numbers = new double[] {
                100, -100, 0.123, 0.1, 12345, -1.34234
        };
        
        PairI format = Util.getFormatNumbers(numbers);
        
        for (double number : numbers)
        {
            println(Util.format(number, format));
        }
    }
}
