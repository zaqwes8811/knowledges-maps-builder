import org.yaml.snakeyaml.Yaml;

public class Main {

    public static void main(String[] args) {
	// write your code here

      String path_to_cfg = "";


      Yaml yaml = new Yaml();
      String document = "  a: 1\n  b:\n    c: 3\n    d: 4\n";
      System.out.println(document);
      System.out.println(yaml.dump(yaml.load(document)));
    }
}
