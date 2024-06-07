/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package egovframework.example.shop.web;

import java.util.List;

import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springmodules.validation.commons.DefaultBeanValidator;

import egovframework.example.shop.service.ShopVO;
import egovframework.example.shop.service.ShopDefaultVO;
import egovframework.example.shop.service.EgovBuyService;


/**
 * @Class Name : EgovSampleController.java
 * @Description : EgovSample Controller Class
 * @Modification Information
 * @
 * @  수정일      수정자              수정내용
 * @ ---------   ---------   -------------------------------
 * @ 2009.03.16           최초생성
 *
 * @author 개발프레임웍크 실행환경 개발팀
 * @since 2009. 03.16
 * @version 1.0
 * @see
 *
 *  Copyright (C) by MOPAS All right reserved.
 */

@Controller
public class EgovBuyController {

	/** EgovBuyService */
	@Resource(name = "buyService")
	private EgovBuyService buyService;

	/** EgovPropertyService */
	@Resource(name = "propertiesService")
	protected EgovPropertyService propertiesService;

	/** Validator */
	@Resource(name = "beanValidator")
	protected DefaultBeanValidator beanValidator;

	/**
	 * 글 목록을 조회한다. (pageing)
	 * @param searchVO - 조회할 정보가 담긴 SampleDefaultVO
	 * @param model
	 * @return "egovSampleList"
	 * @exception Exception
	 */
	@RequestMapping(value = "egovBuyList.do")
	public String selectBuyList(@ModelAttribute("searchVO") ShopDefaultVO searchVO, ModelMap model) throws Exception {

		/** EgovPropertyService.sample */
		searchVO.setPageUnit(propertiesService.getInt("pageUnit"));
		searchVO.setPageSize(propertiesService.getInt("pageSize"));

		/** pageing setting */
		PaginationInfo paginationInfo = new PaginationInfo();
		paginationInfo.setCurrentPageNo(searchVO.getPageIndex());
		paginationInfo.setRecordCountPerPage(searchVO.getPageUnit());
		paginationInfo.setPageSize(searchVO.getPageSize());

		searchVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
		searchVO.setLastIndex(paginationInfo.getLastRecordIndex());
		searchVO.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());

		List<?> buyList = buyService.selectBuyList(searchVO);
		model.addAttribute("resultList", buyList);

		int totCnt = buyService.selectBuyListTotCnt(searchVO);
		paginationInfo.setTotalRecordCount(totCnt);
		model.addAttribute("paginationInfo", paginationInfo);

		return "buy/egovBuyList";
	}

	/**
	 * 글 등록 화면을 조회한다.
	 * @param searchVO - 목록 조회조건 정보가 담긴 VO
	 * @param model
	 * @return "egovBuyRegister"
	 * @exception Exception
	 */
	@RequestMapping(value = "/addBuy.do", method = RequestMethod.GET)
	public String addBuyView(@ModelAttribute("searchVO") ShopDefaultVO searchVO, Model model) throws Exception {
		model.addAttribute("shopVO", new ShopVO());
		
		return "buy/egovBuyRegister";
	}

	/**
	 * 글을 등록한다.
	 * @param sampleVO - 등록할 정보가 담긴 VO
	 * @param searchVO - 목록 조회조건 정보가 담긴 VO
	 * @param status
	 * @return "forward:/egovBuyList.do"
	 * @exception Exception
	 */
	@RequestMapping(value = "/addBuy.do", method = RequestMethod.POST)
	public String addBuy(@ModelAttribute("searchVO") ShopDefaultVO searchVO, ShopVO shopVO, BindingResult bindingResult, Model model, SessionStatus status)
			throws Exception {

		// Server-Side Validation
		beanValidator.validate(shopVO, bindingResult);

		if (bindingResult.hasErrors()) {
			model.addAttribute("shopVO", shopVO);
			return "buy/egovBuyRegister";
		}

		buyService.insertBuy(shopVO);
		status.setComplete();
		return "forward:/egovBuyList.do";
	}

	/**
	 * 글 수정화면을 조회한다.
	 * @param id - 수정할 글 id
	 * @param searchVO - 목록 조회조건 정보가 담긴 VO
	 * @param model
	 * @return "egovSampleRegister"
	 * @exception Exception
	 */
	@RequestMapping("/updateBuyView.do")
	public String updateBuyView(@RequestParam("selectedId") String num, @ModelAttribute("searchVO") ShopDefaultVO searchVO, Model model) throws Exception {
		
		ShopVO shopVO = new ShopVO();
		shopVO.setNum(Integer.parseInt(num));
		
		// 변수명은 CoC 에 따라 sampleVO
		model.addAttribute(selectBuy(shopVO, searchVO));
		
		return "buy/egovBuyRegister";
	}

	/**
	 * 글을 조회한다.
	 * @param sampleVO - 조회할 정보가 담긴 VO
	 * @param searchVO - 목록 조회조건 정보가 담긴 VO
	 * @param status
	 * @return @ModelAttribute("sampleVO") - 조회한 정보
	 * @exception Exception
	 */
	public ShopVO selectBuy(ShopVO shopVO, @ModelAttribute("searchVO") ShopDefaultVO searchVO) throws Exception {
		
		return buyService.selectBuy(shopVO);
	}

	/**
	 * 글을 수정한다.
	 * @param sampleVO - 수정할 정보가 담긴 VO
	 * @param searchVO - 목록 조회조건 정보가 담긴 VO
	 * @param status
	 * @return "forward:/egovSampleList.do"
	 * @exception Exception
	 */
	@RequestMapping("/updateBuy.do")
	public String updateBuy(@ModelAttribute("searchVO") ShopDefaultVO searchVO, ShopVO shopVO, BindingResult bindingResult, Model model, SessionStatus status)
			throws Exception {

		beanValidator.validate(shopVO, bindingResult);

		if (bindingResult.hasErrors()) {
			model.addAttribute("shopVO", shopVO);
			return "buy/egovBuyRegister";
		}

		buyService.updateBuy(shopVO);
		status.setComplete();
		return "forward:/egovBuyList.do";
	}

	/**
	 * 글을 삭제한다.
	 * @param sampleVO - 삭제할 정보가 담긴 VO
	 * @param searchVO - 목록 조회조건 정보가 담긴 VO
	 * @param status
	 * @return "forward:/egovSampleList.do"
	 * @exception Exception
	 */
	@RequestMapping("/deleteBuy.do")
	public String deleteBuy(ShopVO shopVO, @ModelAttribute("searchVO") ShopDefaultVO searchVO, SessionStatus status) throws Exception {
		buyService.deleteBuy(shopVO);
		status.setComplete();
		return "forward:/egovBuyList.do";
	}

}
