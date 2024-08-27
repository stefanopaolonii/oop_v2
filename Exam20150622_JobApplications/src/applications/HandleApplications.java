package applications;

import java.util.*;
import java.util.stream.Collectors;

public class HandleApplications {
	private Map<String,Skill> skillsMap= new HashMap<>();
	private Map<String,Position> positionsMap= new HashMap<>();
	private Map<String,Applicant> applicantsMap= new HashMap<>();
	
	public void addSkills(String... names) throws ApplicationException {
		for(String skill:names){
			if(skillsMap.containsKey(skill)) throw new ApplicationException();
			skillsMap.put(skill, new Skill(skill));
		}
	}
	public void addPosition(String name, String... skillNames) throws ApplicationException {
		if(positionsMap.containsKey(name)) throw new ApplicationException();
		if(!skillsMap.keySet().containsAll(Arrays.asList(skillNames))) throw new ApplicationException();
		positionsMap.put(name, new Position(name, skillNames));
	}
	public Skill getSkill(String name) {return skillsMap.get(name);}
	public Position getPosition(String name) {return positionsMap.get(name);}
	
	public void addApplicant(String name, String capabilities) throws ApplicationException {
		if(applicantsMap.containsKey(name)) throw new ApplicationException();
		Applicant newApplicant= new Applicant(name);
		for(String skill: capabilities.split(",")){
			String[] parts= skill.split(":");
			int level=Integer.parseInt(parts[1]);
			if(!skillsMap.containsKey(parts[0])) throw new ApplicationException();
			if(level<1 || level>10) throw new ApplicationException();
			newApplicant.addSkill(skill, level);
		}
		applicantsMap.put(name, newApplicant);
	}
	public String getCapabilities(String applicantName) throws ApplicationException {
		if(!applicantsMap.containsKey(applicantName)) throw new ApplicationException();
		StringBuffer buffer= new StringBuffer();
		applicantsMap.get(applicantName).getSkillsMap().entrySet().stream().forEach(entry->buffer.append(entry.getKey()).append(":").append(entry.getValue()).append(","));
		if(buffer.length()>0) buffer.deleteCharAt(buffer.length()-1);
		return buffer.toString();
	}
	
	public void enterApplication(String applicantName, String positionName) throws ApplicationException {
		if(!applicantsMap.containsKey(applicantName)) throw new ApplicationException();
		if(!positionsMap.containsKey(positionName)) throw new ApplicationException();
		if(!positionsMap.get(positionName).getSkillsList().containsAll(applicantsMap.get(applicantName).getSkillsMap().keySet())) throw new ApplicationException();
		if(positionsMap.values().stream().anyMatch(position->position.getApplicants().contains(applicantName))) throw new ApplicationException();
		positionsMap.get(positionName).addApplicant(applicantsMap.get(applicantName));
	}
	
	public int setWinner(String applicantName, String positionName) throws ApplicationException {
		return 0;
	}
	
	public SortedMap<String, Long> skill_nApplicants() {
		return null;
	}
	public String maxPosition() {
		return null;
	}
}

