package tech.ggsoft.digitplace.model;

import java.time.LocalTime;

public class Game {
	
	private String userid;
	
	private String quest;
	
	private Integer d1;
	
	private Integer d2;
	
	private Integer d3;
	
	private Integer d4;
	
	private LocalTime localTime;
	
	
	public Game() {
		
	}

	public Game(String userid, String quest, Integer d1, Integer d2, Integer d3, Integer d4, LocalTime localTime) {
		super();
		this.userid = userid;
		this.quest = quest;
		this.d1 = d1;
		this.d2 = d2;
		this.d3 = d3;
		this.d4 = d4;
		this.localTime = localTime;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getQuest() {
		return quest;
	}

	public void setQuest(String quest) {
		this.quest = quest;
	}

	public Integer getD1() {
		return d1;
	}

	public void setD1(Integer d1) {
		this.d1 = d1;
	}

	public Integer getD2() {
		return d2;
	}

	public void setD2(Integer d2) {
		this.d2 = d2;
	}

	public Integer getD3() {
		return d3;
	}

	public void setD3(Integer d3) {
		this.d3 = d3;
	}

	public Integer getD4() {
		return d4;
	}

	public void setD4(Integer d4) {
		this.d4 = d4;
	}

	public LocalTime getLocalTime() {
		return localTime;
	}

	public void setLocalTime(LocalTime localTime) {
		this.localTime = localTime;
	}

	@Override
	public String toString() {
		return "Game [userid=" + userid + ", quest=" + quest + ", d1=" + d1 + ", d2=" + d2 + ", d3=" + d3 + ", d4=" + d4
				+ ", localTime=" + localTime + "]";
	}
	
	
	
}
