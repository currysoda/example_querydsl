package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import static org.assertj.core.api.Assertions.*;
import static study.querydsl.entity.QMember.*;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {
	
	@PersistenceContext
	EntityManager em;
	
	JPAQueryFactory queryFactory;
	
	// 각각 테스트 이전에 실행시켜 준다.
	// 미리 데이터를 넣음
	@BeforeEach
	public void before() {
		queryFactory = new JPAQueryFactory(em);
		
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		em.persist(teamA);
		em.persist(teamB);
		
		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamA);
		Member member3 = new Member("member3", 30, teamB);
		Member member4 = new Member("member4", 40, teamB);
		
		em.persist(member1);
		em.persist(member2);
		em.persist(member3);
		em.persist(member4);
	}
	
	// JPQL 기반
	@Test
	public void startJPQL() {
		// member1 찾기
		// JPQL query 만들기
		String qlString = "select m from Member m " + "where m.username = :username";
		
		Member findMember = em.createQuery(qlString, Member.class)
		                      .setParameter("username", "member1")
		                      .getSingleResult();
		
		assertThat(findMember.getUsername()).isEqualTo("member1");
	}
	
	// Querydsl 기반
	@Test
	public void startQuerydsl() {
		
		// member1 찾기
		Member findMember = queryFactory.select(member)
		                                .from(member)
		                                .where(member.username.eq("member1"))
		                                .fetchOne();
		
		assertThat(findMember.getUsername()).isEqualTo("member1");
	}
	
	
	// 검색 조건 쿼리
	@Test
	public void search() {
		
		// username = member1 & age = 10 찾기
		Member findMember = queryFactory.selectFrom(member)
		                                .where(member.username.eq("member1")
		                                                      .and(member.age.eq(10)))
		                                .fetchOne();
		
		assertThat(findMember.getUsername()).isEqualTo("member1");
	}
	
	
}
