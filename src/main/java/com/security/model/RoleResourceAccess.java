package com.security.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.security.constant.Access;

public class RoleResourceAccess {

	private SimpleGrantedAuthority authority;
	private String resource;
	private Access access;

	public RoleResourceAccess(SimpleGrantedAuthority authority, String resource, Access access) {
		super();
		this.authority = authority;
		this.resource = resource;
		this.access = access;
	}

	public RoleResourceAccess(SimpleGrantedAuthority authority, String resource) {
		super();
		this.authority = authority;
		this.resource = resource;
	}

	public SimpleGrantedAuthority getAuthority() {
		return authority;
	}

	public void setAuthority(SimpleGrantedAuthority authority) {
		this.authority = authority;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public Access getAccess() {
		return access;
	}

	public void setAccess(Access access) {
		this.access = access;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((authority == null) ? 0 : authority.hashCode());
		result = prime * result + ((resource == null) ? 0 : resource.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RoleResourceAccess other = (RoleResourceAccess) obj;
		if (authority == null) {
			if (other.authority != null)
				return false;
		} else if (!authority.equals(other.authority))
			return false;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		return true;
	}

}
