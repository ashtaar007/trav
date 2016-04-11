import java.util.Map;
import java.util.HashMap;
public class Ship {
	double MCr = 0;
	int Tonnage;
	int ExtraTons = 0;
	int MaxThrust;
	int currentThrust;
	Map<String, ShipComponent> components = new HashMap<String, ShipComponent>();
}
