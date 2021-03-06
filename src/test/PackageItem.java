/**
 * Generated by Dalaran
 *   version: 0.0.21
 */
package test;


public class PackageItem {

  private Long userId;
  private Integer packageItemId;
  private Integer balanceAmount;
  private Long acquireAt;
  private Long expireAt;

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getUserId() {
    return this.userId;
  }

  public void setPackageItemId(Integer packageItemId) {
    this.packageItemId = packageItemId;
  }

  public Integer getPackageItemId() {
    return this.packageItemId;
  }

  public void setBalanceAmount(Integer balanceAmount) {
    this.balanceAmount = balanceAmount;
  }

  public Integer getBalanceAmount() {
    return this.balanceAmount;
  }

  public void setAcquireAt(Long acquireAt) {
    this.acquireAt = acquireAt;
  }

  public Long getAcquireAt() {
    return this.acquireAt;
  }

  public void setExpireAt(Long expireAt) {
    this.expireAt = expireAt;
  }

  public Long getExpireAt() {
    return this.expireAt;
  }

  @Override
  public String toString() {
    return "PackageItem(" +"userId=" + this.userId + ", " + "packageItemId=" + this.packageItemId + ", " + "balanceAmount=" + this.balanceAmount + ", " + "acquireAt=" + this.acquireAt + ", " + "expireAt=" + this.expireAt + ")";
  }

  @Override
  public int hashCode() {
    int hash = 1;
    hash = 31 * hash + (this.userId == null ? 0 : this.userId.hashCode());
    hash = 31 * hash + (this.packageItemId == null ? 0 : this.packageItemId.hashCode());
    hash = 31 * hash + (this.balanceAmount == null ? 0 : this.balanceAmount.hashCode());
    hash = 31 * hash + (this.acquireAt == null ? 0 : this.acquireAt.hashCode());
    hash = 31 * hash + (this.expireAt == null ? 0 : this.expireAt.hashCode());
    return hash;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof PackageItem)) {
      return false;
    }
    PackageItem that = (PackageItem) other;
    if (this.userId == null) {
      if (that.userId != null)
        return false;
    } else if (!this.userId.equals(that.userId)) {
      return false;
    }
    if (this.packageItemId == null) {
      if (that.packageItemId != null)
        return false;
    } else if (!this.packageItemId.equals(that.packageItemId)) {
      return false;
    }
    if (this.balanceAmount == null) {
      if (that.balanceAmount != null)
        return false;
    } else if (!this.balanceAmount.equals(that.balanceAmount)) {
      return false;
    }
    if (this.acquireAt == null) {
      if (that.acquireAt != null)
        return false;
    } else if (!this.acquireAt.equals(that.acquireAt)) {
      return false;
    }
    if (this.expireAt == null) {
      if (that.expireAt != null)
        return false;
    } else if (!this.expireAt.equals(that.expireAt)) {
      return false;
    }
    return true;
  }
}