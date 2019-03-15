package cn.cdyxtech.lab.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class UserClaim implements Serializable {
	private Long id;
	private String realName;
	private String mobile;
	private Integer userType;
	private Long[] ecmIds;
	private Long highestEcmId;//根节点ecmId,rootEcmId
	private Long schoolEcmId;
	private Long branchEcmId;
	private Long labEcmId;
	private Long personalHeigherEcmId;//用户当前的最高ecmid
	private Integer highestEcmIdType;

	private Long[] permissionGroupIds;
	private Long[] organizationGroupIds;

	public UserClaim() {
	}

	/**
	 * to Long[]
	 * 
	 * @param list
	 * @return
	 */
	private static Long[] parseJsonArray2Longs(final List<Object> list) {
		Long[] longs = null;
		if (list != null && !list.isEmpty()) {
			longs = list.stream().filter(Objects::nonNull).filter(e -> NumberUtils.isNumber(e.toString()))
					.map(e -> Long.parseLong(e.toString())).toArray(Long[]::new);
		}
		return longs;
	}

	public static UserClaim instance(JSONObject json) {
		JSONObject jsonObject = (JSONObject) JSONObject.toJSON(json);
		JsonObjectHelper helper = new JsonObjectHelper(jsonObject);
		JSONArray ecmIdArr = helper.getValue("ecmIds", JSONArray.class);
		Long[] ecmIds = parseJsonArray2Longs(ecmIdArr);
		JSONArray permissionGroupIdArr = helper.getValue("permissionGroupIds", JSONArray.class);
		Long[] permissionGroupIds = parseJsonArray2Longs(permissionGroupIdArr);
		JSONArray organizationGroupIdsArr = helper.getValue("organizationGroupIds", JSONArray.class);
		Long[] organizationGroupIds = parseJsonArray2Longs(organizationGroupIdsArr);
		return new UserClaim(helper.getValue("id", Long.class), helper.getValue("realName", String.class),
				helper.getValue("mobile", String.class), helper.getValue("userType", Integer.class), ecmIds,
				helper.getValue("highestEcmId", Long.class), helper.getValue("highestEcmIdType", Integer.class),
				permissionGroupIds, organizationGroupIds);
	}
	
	public static UserClaim instance(String json) {
		JSONObject jsonObject = (JSONObject) JSONObject.toJSON(json);
		return instance(jsonObject);
	}

	public UserClaim(Long id, String realName, String mobile, Integer userType, Long[] ecmIds, Long highestEcmId,
			Integer highestEcmIdType,Long schoolEcmId,Long branchEcmId,Long labEcmId, Long personalHeigherEcmId) {
		super();
		this.id = id;
		this.realName = realName;
		this.mobile = mobile;
		this.userType = userType;
		this.ecmIds = ecmIds;
		this.highestEcmId = highestEcmId;
		this.highestEcmIdType = highestEcmIdType;
		this.schoolEcmId = schoolEcmId;
		this.branchEcmId = branchEcmId;
		this.labEcmId = labEcmId;
		this.personalHeigherEcmId = personalHeigherEcmId;

	}

	public UserClaim(Long id, String realName, String mobile, Integer userType, Long[] ecmIds, Long highestEcmId,
			Integer highestEcmIdType, Long[] permissionGroupIds, Long[] organizationGroupIds) {
		super();
		this.id = id;
		this.realName = realName;
		this.mobile = mobile;
		this.userType = userType;
		this.ecmIds = ecmIds;
		this.highestEcmId = highestEcmId;
		this.highestEcmIdType = highestEcmIdType;
		this.permissionGroupIds = permissionGroupIds;
		this.organizationGroupIds = organizationGroupIds;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public Long[] getEcmIds() {
		return ecmIds;
	}

	public void setEcmIds(Long[] ecmIds) {
		this.ecmIds = ecmIds;
	}

	public Long getHighestEcmId() {
		return highestEcmId;
	}

	public void setHighestEcmId(Long highestEcmId) {
		this.highestEcmId = highestEcmId;
	}

	public Integer getHighestEcmIdType() {
		return highestEcmIdType;
	}

	public void setHighestEcmIdType(Integer highestEcmIdType) {
		this.highestEcmIdType = highestEcmIdType;
	}

	public Long[] getPermissionGroupIds() {
		return permissionGroupIds;
	}

	public void setPermissionGroupIds(Long[] permissionGroupIds) {
		this.permissionGroupIds = permissionGroupIds;
	}

	public Long[] getOrganizationGroupIds() {
		return organizationGroupIds;
	}

	public void setOrganizationGroupIds(Long[] organizationGroupIds) {
		this.organizationGroupIds = organizationGroupIds;
	}
	public Long getSchoolEcmId() {
		return schoolEcmId;
	}

	public void setSchoolEcmId(Long schoolEcmId) {
		this.schoolEcmId = schoolEcmId;
	}
	public Long getBranchEcmId() {
		return branchEcmId;
	}

	public void setBranchEcmId(Long branchEcmId) {
		this.branchEcmId = branchEcmId;
	}
	public Long getLabEcmId() {
		return labEcmId;
	}

	public void setLabEcmId(Long labEcmId) {
		this.labEcmId = labEcmId;
	}
	public Long getPersonalHeigherEcmId() {
		return personalHeigherEcmId;
	}

	public void setPersonalHeigherEcmId(Long personalHeigherEcmId) {
		this.personalHeigherEcmId = personalHeigherEcmId;
	}

}