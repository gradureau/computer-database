package com.excilys.gradureau.computer_database.persistance.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.excilys.gradureau.computer_database.model.Company;
import com.excilys.gradureau.computer_database.model.Company_;
import com.excilys.gradureau.computer_database.model.Computer;
import com.excilys.gradureau.computer_database.model.Computer_;
import com.excilys.gradureau.computer_database.util.Page;

@Repository
@Transactional(readOnly=true)
public class CompanyDAO extends DAO<Company> {
    /*
     * https://docs.oracle.com/javaee/6/tutorial/doc/gjivm.html
     */
    
    @Autowired
    EntityManager entityManager;

    @Override
    public Optional<Company> find(long id) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Company> cquery = cb.createQuery(Company.class);
            Root<Company> companyNode = cquery.from(Company.class);
            ParameterExpression<Long> idParameter = cb.parameter(Long.class, "id");
            cquery.select(companyNode).where(cb.equal(companyNode.get(Company_.ID), idParameter));
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
    public Optional<Company> create(Company obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Transactional(readOnly=false)
    public Optional<Company> update(Company obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Transactional(readOnly=false)
    public boolean delete(Company company) {
        entityManager.joinTransaction();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        CriteriaDelete<Computer> cdeleteChildren =  cb.createCriteriaDelete(Computer.class);
        ParameterExpression<Company> companyParameter = cb.parameter(Company.class, "company");
        Root<Computer> computerNode = cdeleteChildren.from(Computer.class);
        cdeleteChildren.where(cb.equal(computerNode.get(Computer_.COMPANY), companyParameter));
        Query query = entityManager.createQuery(cdeleteChildren).setParameter("company", company);
        query.executeUpdate();
        
        CriteriaDelete<Company> cdelete =  cb.createCriteriaDelete(Company.class);
        ParameterExpression<Long> companyIdParameter = cb.parameter(Long.class, "companyId");
        Root<Company> companyNode = cdelete.from(Company.class);
        cdelete.where(cb.equal(companyNode.get(Company_.ID), companyIdParameter));
        return entityManager.createQuery(cdelete).setParameter("companyId", company.getId())
                .executeUpdate() == 1;
    }

    @Override
    public List<Company> findAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Company> cquery = cb.createQuery(Company.class);
        Root<Company> companyNode = cquery.from(Company.class);
        cquery.select(companyNode);
        cquery.orderBy(cb.asc(companyNode.get(Company_.NAME)));
        return entityManager.createQuery(cquery).getResultList();
    }

    @Override
    public Page<Company> pagination(int start, int resultsCount) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Company> cquery = cb.createQuery(Company.class);
        Root<Company> companyNode = cquery.from(Company.class);
        cquery.select(companyNode);
        cquery.orderBy(cb.asc(companyNode.get(Company_.NAME)));
        TypedQuery<Company> query = entityManager
                .createQuery(cquery)
                .setFirstResult(start)
                .setMaxResults(resultsCount);
        List<Company> companies = query.getResultList();
        return new Page<>(companies, start, resultsCount);
    }

    @Override
    public Page<Company> filterBy(Map<String, String> criterias, int start, int resultsCount, boolean inclusive) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long count() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cquery = cb.createQuery(Long.class);
        cquery.select(cb.count(cquery.from(Company.class)));
        return entityManager.createQuery(cquery).getSingleResult();
    }

}
