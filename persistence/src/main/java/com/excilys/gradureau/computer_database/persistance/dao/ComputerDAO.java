package com.excilys.gradureau.computer_database.persistance.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.excilys.gradureau.computer_database.model.Company_;
import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.model.Computer_;
import com.excilys.gradureau.computer_database.util.Page;

@Repository
@Transactional(readOnly=true)
public class ComputerDAO extends DAO<Computer> {
    
    @Autowired
    EntityManager entityManager;
    
    public static enum Fields {
        COMPUTER_NAME(Computer_.NAME),
        COMPANY_NAME(Company_.NAME);
        
        private String sqlAlias;
        Fields(String sqlAlias) {
            this.sqlAlias = sqlAlias;
        }
        public String getSqlAlias() {
            return sqlAlias;
        }
    }

    @Override
    public Optional<Computer> find(long id) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Computer> cquery = cb.createQuery(Computer.class);
            Root<Computer> computerNode = cquery.from(Computer.class);
            ParameterExpression<Long> idParameter = cb.parameter(Long.class, "id");
            cquery.select(computerNode).where(cb.equal(computerNode.get(Computer_.ID), idParameter));
            return Optional.of(
                    entityManager
                    .createQuery(cquery)
                    .setParameter("id", id)
                    .getSingleResult()
                    );
        } catch(NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly=false)
    public Optional<Computer> create(Computer computer) {
        entityManager.joinTransaction();
        entityManager.persist(computer);
        entityManager.flush();
        entityManager.refresh(computer);
        entityManager.clear();
        return Optional.of(computer);
    }

    @Override
    @Transactional(readOnly=false)
    public Optional<Computer> update(Computer computer) {
        /*
         * Might as well call entityManager::merge
         */
        entityManager.joinTransaction();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Computer> cupdate =  cb.createCriteriaUpdate(Computer.class);
        Root<Computer> computerNode = cupdate.from(Computer.class);
        ParameterExpression<Long> computerIdParameter = cb.parameter(Long.class, "computerId");
        cupdate
        .set(computerNode.get(Computer_.NAME), computer.getName())
        .set(computerNode.get(Computer_.INTRODUCED), computer.getIntroduced())
        .set(computerNode.get(Computer_.DISCONTINUED), computer.getDiscontinued())
        .set(computerNode.get(Computer_.COMPANY), computer.getCompany())
        .where(cb.equal(computerNode.get(Computer_.ID), computerIdParameter));
        
        Query query = entityManager.createQuery(cupdate).setParameter("computerId", computer.getId());
        Computer managedComputer = entityManager.merge(computer);
        entityManager.flush();
        entityManager.refresh(managedComputer);
        entityManager.clear();
        boolean wasUpdated = query.executeUpdate() == 1;
        return wasUpdated ? Optional.of(managedComputer) : Optional.empty();
    }

    @Override
    @Transactional(readOnly=false)
    public boolean delete(Computer computer) {
        entityManager.joinTransaction();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<Computer> cdelete =  cb.createCriteriaDelete(Computer.class);
        ParameterExpression<Long> computerIdParameter = cb.parameter(Long.class, "computerId");
        Root<Computer> computerNode = cdelete.from(Computer.class);
        cdelete.where(cb.equal(computerNode.get(Computer_.ID), computerIdParameter));
        return entityManager.createQuery(cdelete).setParameter("computerId", computer.getId())
                .executeUpdate() == 1;
    }

    @Override
    public List<Computer> findAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Computer> cquery = cb.createQuery(Computer.class);
        return entityManager.createQuery(
                cquery.select(cquery.from(Computer.class))
                ).getResultList();
    }

    @Override
    public Page<Computer> pagination(int start, int resultsCount) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Computer> cquery = cb.createQuery(Computer.class);
        Root<Computer> computerNode = cquery.from(Computer.class);
        cquery.select(computerNode);
        cquery.orderBy(
                cb.desc(computerNode.get(Computer_.INTRODUCED)),
                cb.asc(computerNode.get(Computer_.ID)));
        List<Computer> computers = entityManager
                .createQuery(cquery)
                .setFirstResult(start)
                .setMaxResults(resultsCount)
                .getResultList();
        Page<Computer> page = new Page<>(computers, start, resultsCount);
        page.setPageable(this::pagination);
        page.setTotalResultsCounter(this::count);
        return  page;
    }
    
    @Override
    public long count() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cquery = cb.createQuery(Long.class);
        cquery.select(cb.count(cquery.from(Computer.class)));
        return entityManager.createQuery(cquery).getSingleResult();
    }

    @Override
    public Page<Computer> filterBy(Map<String, String> criterias, int start, int resultsCount, boolean inclusive) {
        
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Computer> cquery = cb.createQuery(Computer.class);
        Root<Computer> computerNode = cquery.from(Computer.class);
        
        Optional<Predicate> optionalRestrictions = criterias.entrySet().stream().map(
                (entry) -> {
                    String field = entry.getKey();
                    String searchedKeyWords = entry.getValue();
                    return cb.like(computerNode.get(field), "%"+searchedKeyWords+"%");
                }).reduce(
                        (e1, e2) -> inclusive ? cb.or(e1, e2) : cb.and(e1, e2)
                        );
        
        cquery.select(computerNode);
        if(optionalRestrictions.isPresent())
            cquery.where(optionalRestrictions.get());
        cquery.orderBy(
                cb.desc(computerNode.get(Computer_.INTRODUCED)),
                cb.asc(computerNode.get(Computer_.ID)));
        
        List<Computer> filteredComputers = entityManager
                .createQuery(cquery)
                .setFirstResult(start)
                .setMaxResults(resultsCount)
                .getResultList();
                
        Page<Computer> page = new Page<>(filteredComputers, start, resultsCount);
        page.setPageable( (_start, _resultsCount) -> filterBy(criterias, _start, _resultsCount) );
        page.setTotalResultsCounter( () -> {
            return countFilteredResults(optionalRestrictions);
        });
        return  page;
    }
    
    private Long countFilteredResults(Optional<Predicate> optionalRestrictions) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cquery = cb.createQuery(Long.class);
        Root<Computer> computerNode = cquery.from(Computer.class);
        cquery.select(cb.count(computerNode));
        if(optionalRestrictions.isPresent())
            cquery.where(optionalRestrictions.get());
        return entityManager.createQuery(cquery).getSingleResult();
    }

}
