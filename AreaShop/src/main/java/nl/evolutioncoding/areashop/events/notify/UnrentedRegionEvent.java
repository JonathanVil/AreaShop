package nl.evolutioncoding.areashop.events.notify;

import nl.evolutioncoding.areashop.events.NotifyAreaShopEvent;
import nl.evolutioncoding.areashop.regions.RentRegion;

import java.util.UUID;

/**
 * Broadcasted when a region has been unrented
 */
public class UnrentedRegionEvent extends NotifyAreaShopEvent {

	private RentRegion region;
	private UUID oldRenter;
	private double refundedMoney;

	/**
	 * Constructor
	 * @param region    The region that has been unrented
	 * @param oldRenter The player that rented the region before it was unrented
	 */
	public UnrentedRegionEvent(RentRegion region, UUID oldRenter, double refundedMoney) {
		this.region = region;
		this.oldRenter = oldRenter;
		this.refundedMoney = refundedMoney;
	}

	/**
	 * Get the region that has been unrented
	 * @return the region that has been unrented
	 */
	public RentRegion getRegion() {
		return region;
	}

	/**
	 * Get the player that the region was unrented for
	 * @return The UUID of the player that the region was unrented for
	 */
	public UUID getOldRenter() {
		return oldRenter;
	}

	/**
	 * Get the amount that is paid back to the player
	 * @return The amount of money paid back to the player
	 */
	public double getRefundedMoney() {
		return refundedMoney;
	}
}
