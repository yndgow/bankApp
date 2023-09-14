package com.tencoding.bank.repository.interfaces;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.tencoding.bank.repository.model.History;

// ibatis -> 2.4버전 이후로 Mybatis로 이름이 변경됨
@Mapper // Mapper 반드시 기술을 해줘야 동작한다.
public interface HistoryRepository {
	
	// 뱅크 앱. 기록과 관련된 기능들
	public int insert(History history); // 입력(계좌이체)
	public int updateById(History history); // 수정
	public int deleteById(Integer Id); // 삭제
	public History findById(Integer id); // 찾기 one
	public List<History> findAll(); // 찾기 all
	public List<History> findAllForWithdrawByWid(Integer principalId); // 찾기 입금
	public List<History> findAllForDepositByDid(Integer principalId); // 찾기 출금
}