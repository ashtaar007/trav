import java.util.Map;
import java.util.HashMap;
public class Ship {
	double MCr = 0;
	double HullBaseCost = 0;
	int HullBaseTons = 0;
	double ExtraTons = 0;
	int maxThrust;
	int currentThrust;
	Map<String, ShipComponent> components = new HashMap<String, ShipComponent>();
}
