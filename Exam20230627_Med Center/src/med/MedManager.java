package med;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class MedManager {
	private Set<String> specialitiesSet= new HashSet<>();
	private Map<String,Doctor> doctorsMap= new HashMap<>();
	private Map<String,Appointment> appointmentsMap= new HashMap<>();
	private Map<String,Patient> patientsMap= new HashMap<>();
	private int appointmentCounter=0;
	private String currentDate;
	/**
	 * add a set of medical specialities to the list of specialities
	 * offered by the med centre.
	 * Method can be invoked multiple times.
	 * Possible duplicates are ignored.
	 * 
	 * @param specialities the specialities
	 */
	public void addSpecialities(String... specialities) {
		specialitiesSet.addAll(Arrays.asList(specialities));
	}

	/**
	 * retrieves the list of specialities offered in the med centre
	 * 
	 * @return list of specialities
	 */
	public Collection<String> getSpecialities() {
		return specialitiesSet;
	}
	
	
	/**
	 * adds a new doctor with the list of their specialities
	 * 
	 * @param id		unique id of doctor
	 * @param name		name of doctor
	 * @param surname	surname of doctor
	 * @param speciality speciality of the doctor
	 * @throws MedException in case of duplicate id or non-existing speciality
	 */
	public void addDoctor(String id, String name, String surname, String speciality) throws MedException {
		if(doctorsMap.containsKey(id)) throw new MedException();
		if(!specialitiesSet.contains(speciality)) throw new MedException();
		doctorsMap.put(id, new Doctor(id, name, surname, speciality));
	}

	/**
	 * retrieves the list of doctors with the given speciality
	 * 
	 * @param speciality required speciality
	 * @return the list of doctor ids
	 */
	public Collection<String> getSpecialists(String speciality) {
		return doctorsMap.values().stream().filter(doctor->doctor.getSpeciality().equals(speciality)).map(Doctor::getId).collect(Collectors.toList());
	}

	/**
	 * retrieves the name of the doctor with the given code
	 * 
	 * @param code code id of the doctor 
	 * @return the name
	 */
	public String getDocName(String code) {
		if(!doctorsMap.containsKey(code)) return null;
		return doctorsMap.get(code).getName();
	}

	/**
	 * retrieves the surname of the doctor with the given code
	 * 
	 * @param code code id of the doctor 
	 * @return the surname
	 */
	public String getDocSurname(String code) {
		if(!doctorsMap.containsKey(code)) return null;
		return doctorsMap.get(code).getSurname();
	}

	/**
	 * Define a schedule for a doctor on a given day.
	 * Slots are created between start and end hours with a 
	 * duration expressed in minutes.
	 * 
	 * @param code	doctor id code
	 * @param date	date of schedule
	 * @param start	start time
	 * @param end	end time
	 * @param duration duration in minutes
	 * @return the number of slots defined
	 */
	public int addDailySchedule(String code, String date, String start, String end, int duration) {
		String[] startparts=start.split(":");
		String[] endparts=end.split(":");
		int counter=0;
		int startinminutes=Integer.parseInt(startparts[0])*60+Integer.parseInt(startparts[1]);
		int endinminutes=Integer.parseInt(endparts[0])*60+Integer.parseInt(endparts[1]);
		while(startinminutes<endinminutes){
			doctorsMap.get(code).addSlot(new Slot(date, String.format("%02d:%02d",startinminutes/60,startinminutes%60), String.format("%02d:%02d",(startinminutes+duration)/60,(startinminutes+duration)%60), duration));
			startinminutes+=duration;
			counter++;
		}
		return counter;
	}

	/**
	 * retrieves the available slots available on a given date for a speciality.
	 * The returned map contains an entry for each doctor that has slots scheduled on the date.
	 * The map contains a list of slots described as strings with the format "hh:mm-hh:mm",
	 * e.g. "14:00-14:30" describes a slot starting at 14:00 and lasting 30 minutes.
	 * 
	 * @param date			date to look for
	 * @param speciality	required speciality
	 * @return a map doc-id -> list of slots in the schedule
	 */
	public Map<String, List<String>> findSlots(String date, String speciality) {
		return doctorsMap.values().stream().filter(doctor->!doctor.getSlotsList().isEmpty() && doctor.getSpeciality().equals(speciality)).collect(Collectors.toMap(Doctor::getId, doc-> doc.getSlotsList().stream().filter(slot->slot.getDate().equals(date)).map(slot->slot.getStartTime()+"-"+slot.getEndTime()).collect(Collectors.toList())));

		}

	/**
	 * Define an appointment for a patient in an existing slot of a doctor's schedule
	 * 
	 * @param ssn		ssn of the patient
	 * @param name		name of the patient
	 * @param surname	surname of the patient
	 * @param code		code id of the doctor
	 * @param date		date of the appointment
	 * @param slot		slot to be booked
	 * @return a unique id for the appointment
	 * @throws MedException	in case of invalid code, date or slot
	 */
	public String setAppointment(String ssn, String name, String surname, String code, String date, String slot) throws MedException {
		if(!doctorsMap.containsKey(code)) throw new MedException();
		String[] slotparts= slot.split("-");
		Slot tmpSlot= new Slot(date, slotparts[0], slotparts[1], 0);
		if(!doctorsMap.get(code).getSlotsList().stream().anyMatch(sslot-> sslot.equals(tmpSlot))) throw new MedException();
		if(!patientsMap.containsKey(ssn)) patientsMap.put(ssn, new Patient(ssn, name, surname));
		String id=String.format("A%d", appointmentCounter++);
		appointmentsMap.put(id, new Appointment(patientsMap.get(ssn), doctorsMap.get(code), id, tmpSlot));
		return id;
	}

	/**
	 * retrieves the doctor for an appointment
	 * 
	 * @param idAppointment id of appointment
	 * @return doctor code id
	 */
	public String getAppointmentDoctor(String idAppointment) {
		if(!appointmentsMap.containsKey(idAppointment)) return null;
		return appointmentsMap.get(idAppointment).getDoctor().getId();
	}

	/**
	 * retrieves the patient for an appointment
	 * 
	 * @param idAppointment id of appointment
	 * @return doctor patient ssn
	 */
	public String getAppointmentPatient(String idAppointment) {
		if(!appointmentsMap.containsKey(idAppointment)) return null;
		return appointmentsMap.get(idAppointment).getPatient().getSsn();
	}

	/**
	 * retrieves the time for an appointment
	 * 
	 * @param idAppointment id of appointment
	 * @return time of appointment
	 */
	public String getAppointmentTime(String idAppointment) {
		if(!appointmentsMap.containsKey(idAppointment)) return null;
		return appointmentsMap.get(idAppointment).getSlot().getStartTime();
	}

	/**
	 * retrieves the date for an appointment
	 * 
	 * @param idAppointment id of appointment
	 * @return date
	 */
	public String getAppointmentDate(String idAppointment) {
		if(!appointmentsMap.containsKey(idAppointment)) return null;
		return appointmentsMap.get(idAppointment).getSlot().getDate();
	}

	/**
	 * retrieves the list of a doctor appointments for a given day.
	 * Appointments are reported as string with the format
	 * "hh:mm=SSN"
	 * 
	 * @param code doctor id
	 * @param date date required
	 * @return list of appointments
	 */
	public Collection<String> listAppointments(String code, String date) {
		return appointmentsMap.values().stream().filter(appointment->appointment.getSlot().getDate().equals(date) && appointment.getDoctor().getId().equals(code)).map(appointment->appointment.getSlot().getStartTime()+"="+appointment.getPatient().getSsn()).collect(Collectors.toList());
	}

	/**
	 * Define the current date for the medical centre
	 * The date will be used to accept patients arriving at the centre.
	 * 
	 * @param date	current date
	 * @return the number of total appointments for the day
	 */
	public int setCurrentDate(String date) {
		this.currentDate=date;
		return (int) appointmentsMap.values().stream().filter(appointment->appointment.getSlot().getDate().equals(date)).count();
	}

	/**
	 * mark the patient as accepted by the med centre reception
	 * 
	 * @param ssn SSN of the patient
	 */
	public void accept(String ssn) {
		if(!patientsMap.containsKey(ssn)) return;
		patientsMap.get(ssn).setAccepted(true);
	}

	/**
	 * returns the next appointment of a patient that has been accepted.
	 * Returns the id of the earliest appointment whose patient has been
	 * accepted and the appointment not completed yet.
	 * Returns null if no such appointment is available.
	 * 
	 * @param code	code id of the doctor
	 * @return appointment id
	 */
	public String nextAppointment(String code) {
		return appointmentsMap.values().stream().filter(appointment->appointment.getDoctor().getId().equals(code) && appointment.getSlot().getDate().equals(currentDate) && appointment.getPatient().isAccepted() && !appointment.isCompleted()).map(Appointment::getId).sorted().findFirst().orElse(null);
	}

	/**
	 * mark an appointment as complete.
	 * The appointment must be with the doctor with the given code
	 * the patient must have been accepted
	 * 
	 * @param code		doctor code id
	 * @param appId		appointment id
	 * @throws MedException in case code or appointment code not valid,
	 * 						or appointment with another doctor
	 * 						or patient not accepted
	 * 						or appointment not for the current day
	 */
	public void completeAppointment(String code, String appId)  throws MedException {
		if(!appointmentsMap.containsKey(appId)) throw new MedException();
		if(!doctorsMap.containsKey(code)) throw new MedException();
		Appointment searchedapp= appointmentsMap.get(appId);
		if(!searchedapp.getDoctor().getId().equals(code)) throw new MedException();
		if(!searchedapp.getPatient().isAccepted()) throw new MedException();
		if(!searchedapp.getSlot().getDate().equals(currentDate)) throw new MedException();
		searchedapp.setCompleted(true);
	}

	/**
	 * computes the show rate for the appointments of a doctor on a given date.
	 * The rate is the ratio of accepted patients over the number of appointments
	 *  
	 * @param code		doctor id
	 * @param date		reference date
	 * @return	no show rate
	 */
	public double showRate(String code, String date) {
		List<Appointment> searchedapp= appointmentsMap.values().stream().filter(appointment->appointment.getDoctor().getId().equals(code) && appointment.getSlot().getDate().equals(date)).collect(Collectors.toList());
		return (double)searchedapp.stream().filter(app->app.getPatient().isAccepted()).count()/searchedapp.size();
	}

	/**
	 * computes the schedule completeness for all doctors of the med centre.
	 * The completeness for a doctor is the ratio of the number of appointments
	 * over the number of slots in the schedule.
	 * The result is a map that associates to each doctor id the relative completeness
	 * 
	 * @return the map id : completeness
	 */
	public Map<String, Double> scheduleCompleteness() {
		return doctorsMap.values().stream().collect(Collectors.toMap(Doctor::getId, doc->appointmentsMap.values().stream().filter(app->app.getDoctor().equals(doc)).count()/(double)doc.getSlotsList().size()));
	}


	
}
