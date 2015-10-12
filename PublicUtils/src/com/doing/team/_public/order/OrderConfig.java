package com.doing.team._public.order;

import java.util.List;

/**
 * 订单相关配置
 * @author wangzefeng
 *
 */
public class OrderConfig {

	/**
	 * 订单注入js
	 */
	private OrderInjectJs[] orderInjectJs;
	
	/**
	 * 我的订单相关
	 */
	private OrderReg[] order;
	
	public OrderInjectJs[] getOrderInjectJs() {
		return orderInjectJs;
	}

	public void setOrderInjectJs(OrderInjectJs[] orderInjectJs) {
		this.orderInjectJs = orderInjectJs;
	}

	public OrderReg[] getOrder() {
		return order;
	}

	public void setOrder(OrderReg[] order) {
		this.order = order;
	}
	
    /**
     * 订单相关
     */
    public static class OrderReg {
    	private String name;
        private List<OrderType> type;
    	
        public static class OrderType{
        	private List<PatternRules> typePattern;
        	private OrderPattern orderPattern;
        	
        	public static class PatternRules{
				private int group;
				private String pattern;
				
				public int getGroup() {
					return group;
				}
				public void setGroup(int group) {
					this.group = group;
				}
				public String getPattern() {
					return pattern;
				}
				public void setPattern(String pattern) {
					this.pattern = pattern;
				}
				
			}
        	
        	public static class OrderPattern{
        		private String orderType;
        		private String orderTypeColor;
        		private String businessName;
    			private List<PatternRules> titleName;
    			private List<PatternRules> orderDetail;
    			private List<PatternRules> orderPwd;
    			private List<PatternRules> businessPhone;
    			private List<PatternRules> personName;
    			private List<PatternRules> orderNum;
    			private List<PatternRules> serialNum;
    			private List<PatternRules> validTime;
    			private List<PatternRules> verfyCode;
    			private List<PatternRules> consumerCode;
    			private List<PatternRules> orderTime;
    			private List<PatternRules> day;
    			private List<PatternRules> carNum;
    			private List<PatternRules> ticketNum;
    			private List<PatternRules> seat;
    			private List<PatternRules> seatType;
    			private List<PatternRules> hotel;
    			private List<PatternRules> address;
    			private List<PatternRules> room;
    			private List<PatternRules> price;
    			private UrlPattern url;
    			private List<PatternRules> isUsed;
				
    			
    			public String getOrderType() {
					return orderType;
				}


				public void setOrderType(String orderType) {
					this.orderType = orderType;
				}


				public String getOrderTypeColor() {
					return orderTypeColor;
				}


				public void setOrderTypeColor(String orderTypeColor) {
					this.orderTypeColor = orderTypeColor;
				}

				public String getBusinessName() {
					return businessName;
				}


				public void setBusinessName(String businessName) {
					this.businessName = businessName;
				}


				public List<PatternRules> getTitleName() {
					return titleName;
				}


				public void setTitleName(List<PatternRules> titleName) {
					this.titleName = titleName;
				}


				public List<PatternRules> getOrderDetail() {
					return orderDetail;
				}


				public void setOrderDetail(List<PatternRules> orderDetail) {
					this.orderDetail = orderDetail;
				}


				public List<PatternRules> getOrderPwd() {
					return orderPwd;
				}


				public void setOrderPwd(List<PatternRules> orderPwd) {
					this.orderPwd = orderPwd;
				}


				public List<PatternRules> getBusinessPhone() {
					return businessPhone;
				}


				public void setBusinessPhone(List<PatternRules> businessPhone) {
					this.businessPhone = businessPhone;
				}


				public List<PatternRules> getPersonName() {
					return personName;
				}


				public void setPersonName(List<PatternRules> personName) {
					this.personName = personName;
				}


				public List<PatternRules> getOrderNum() {
					return orderNum;
				}


				public void setOrderNum(List<PatternRules> orderNum) {
					this.orderNum = orderNum;
				}


				public List<PatternRules> getSerialNum() {
					return serialNum;
				}


				public void setSerialNum(List<PatternRules> serialNum) {
					this.serialNum = serialNum;
				}
				

				public List<PatternRules> getValidTime() {
					return validTime;
				}


				public void setValidTime(List<PatternRules> validTime) {
					this.validTime = validTime;
				}


				public List<PatternRules> getVerfyCode() {
					return verfyCode;
				}


				public void setVerfyCode(List<PatternRules> verfyCode) {
					this.verfyCode = verfyCode;
				}


				public List<PatternRules> getConsumerCode() {
					return consumerCode;
				}


				public void setConsumerCode(List<PatternRules> consumerCode) {
					this.consumerCode = consumerCode;
				}


				public List<PatternRules> getOrderTime() {
					return orderTime;
				}


				public void setOrderTime(List<PatternRules> orderTime) {
					this.orderTime = orderTime;
				}


				public List<PatternRules> getDay() {
					return day;
				}


				public void setDay(List<PatternRules> day) {
					this.day = day;
				}


				public List<PatternRules> getCarNum() {
					return carNum;
				}


				public void setCarNum(List<PatternRules> carNum) {
					this.carNum = carNum;
				}


				public List<PatternRules> getTicketNum() {
					return ticketNum;
				}


				public void setTicketNum(List<PatternRules> ticketNum) {
					this.ticketNum = ticketNum;
				}


				public List<PatternRules> getSeat() {
					return seat;
				}


				public void setSeat(List<PatternRules> seat) {
					this.seat = seat;
				}


				public List<PatternRules> getSeatType() {
					return seatType;
				}


				public void setSeatType(List<PatternRules> seatType) {
					this.seatType = seatType;
				}


				public List<PatternRules> getHotel() {
					return hotel;
				}


				public void setHotel(List<PatternRules> hotel) {
					this.hotel = hotel;
				}


				public List<PatternRules> getAddress() {
					return address;
				}


				public void setAddress(List<PatternRules> address) {
					this.address = address;
				}


				public List<PatternRules> getRoom() {
					return room;
				}


				public void setRoom(List<PatternRules> room) {
					this.room = room;
				}


				public List<PatternRules> getPrice() {
					return price;
				}


				public void setPrice(List<PatternRules> price) {
					this.price = price;
				}


				public UrlPattern getUrl() {
					return url;
				}


				public void setUrl(UrlPattern url) {
					this.url = url;
				}


				public List<PatternRules> getIsUsed() {
					return isUsed;
				}


				public void setIsUsed(List<PatternRules> isUsed) {
					this.isUsed = isUsed;
				}


				public static class UrlPattern{
    				private List<PatternRules> urlSmsPattern;
    				private List<PatternRules> urlLinkPattern;
    				private String urlDefault;
					public List<PatternRules> getUrlSmsPattern() {
						return urlSmsPattern;
					}
					public void setUrlSmsPattern(List<PatternRules> urlSmsPattern) {
						this.urlSmsPattern = urlSmsPattern;
					}
					public List<PatternRules> getUrlLinkPattern() {
						return urlLinkPattern;
					}
					public void setUrlLinkPattern(List<PatternRules> urlLinkPattern) {
						this.urlLinkPattern = urlLinkPattern;
					}
					public String getUrlDefault() {
						return urlDefault;
					}
					public void setUrlDefault(String urlDefault) {
						this.urlDefault = urlDefault;
					}
    				
    				
    			}
        	}

			public List<PatternRules> getTypePattern() {
				return typePattern;
			}

			public void setTypePattern(List<PatternRules> typePattern) {
				this.typePattern = typePattern;
			}

			public OrderPattern getOrderPattern() {
				return orderPattern;
			}

			public void setOrderPattern(OrderPattern orderPattern) {
				this.orderPattern = orderPattern;
			}
        		
    	}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<OrderType> getType() {
			return type;
		}

		public void setType(List<OrderType> type) {
			this.type = type;
		}
		
		
        
    }
    
    public static class OrderInjectJs{
    	private List<String> pattern;
    	private String inject_js;
    	public OrderInjectJs(){
    		
    	}
		public List<String> getPattern() {
			return pattern;
		}
		public void setPattern(List<String> pattern) {
			this.pattern = pattern;
		}
		public String getInject_js() {
			return inject_js;
		}
		public void setInject_js(String inject_js) {
			this.inject_js = inject_js;
		}
    	
    }
}
