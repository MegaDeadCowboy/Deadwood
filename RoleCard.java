import java.util.List;

//contain Role Card info to be added into rooms
public class RoleCard {
    private int sceneID;
    private String sceneName;
    private int totalShots;
    private int sceneBudget;
    private List<String> sceneRoles;


    public boolean validateRole(String roleName, int currnetRank){
        //find roleRank from roleName
        if (currentRank => roleRank){
            return true;
        }
        else {
            return false;
        }
    }
    //getters
    public int getSceneBudget(){
        return sceneBudget;
    }
}
